package com.alperez.sunmi.pos.receiptengine.print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.Nullable;

import com.alperez.sunmi.pos.BuildConfig;
import com.alperez.sunmi.pos.R;
import com.alperez.sunmi.pos.utils.IOUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings({"CatchMayIgnoreException", "StaticFieldLeak"})
final class SunmiBtPrinter implements PosPrinter {

    private static final String INNER_PRINTER_ADDRESS = "00:11:22:33:44:55";
    private static final UUID PRINTER_SERVICE_CHANNEL_ID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private static SunmiBtPrinter instance;

    public static SunmiBtPrinter getInstance(Context ctx) {
        if (instance == null) {
            synchronized (SunmiBtPrinter.class) {
                if (instance == null) {
                    instance = new SunmiBtPrinter(ctx.getApplicationContext());
                }
            }
        }
        return instance;
    }

    //This is the Application Context -> no mem. leak
    private final Context context;
    private final LinkedBlockingDeque<PrintTask> tasks = new LinkedBlockingDeque<>();
    private final Object trigger = new Object();

    private Thread worker;
    private final AtomicBoolean released = new AtomicBoolean(false);

    private SunmiBtPrinter(Context ctx) {
        this.context = ctx;
    }


    @Override
    public PosPrinterParams getPrinterParams() {
        return new PosPrinterParams() {
            public boolean isUnidirectionPrintSupported() {
                return false;
            }

            @Override
            public int[] characterScaleWidthLimits() {
                return new int[]{1, 4};
            }

            @Override
            public int[] characterScaleHeightLimits() {
                return new int[]{1, 5};
            }

            @Override
            public int lineLengthFromScaleWidth(int scaleWidth) {
                switch (scaleWidth) {
                    case 1:
                        return 32;
                    case 2:
                        return 16;
                    case 3:
                        return 10;
                    case 4:
                        return 8;
                    default:
                        throw new IllegalArgumentException("Width scale is not supported - "+scaleWidth+". Allowed [1, 4]");
                }
            }
        };
    }

    public void print(Collection<byte[]> data, PrintResultCallback callback) {
        tasks.addLast(new PrintTask(data, callback));
        synchronized (trigger) {
            if(worker == null) {
                released.set(false);
                worker = new Thread(this::work, "sunmi-bt-printer-worker");
                worker.start();
            }
            trigger.notify();
        }
    }

    /**
     * This method notifies the worker thread to stop. All jobs waiting in the queue are deleted.
     */
    public void shutdown() {
        synchronized (trigger) {
            tasks.clear();
            if (worker != null) {
                released.set(true);
                worker.interrupt();
                worker = null;
            }
        }
    }


    /**
     * This method is supposed to be used in a single thread.
     * So there're no extra thread-safe approaches for printer interface.
     */
    private void work() {
        //The endpoint for communication with printer.
        OutputStream print_os = null;

main_cycle: while (!released.get()) {
            PrintTask task;
            synchronized (trigger) {
                while((task = tasks.pollFirst()) == null) {

                    // Close printer because of empty work queue
                    if(print_os != null) {
                        IOUtils.cloaeSilently(print_os); //This will also close associated Socket
                        print_os = null;
                    }

                    // Wait for new jobs
                    try {
                        trigger.wait(1000);
                    } catch (InterruptedException e) {
                        break main_cycle; // Check for 'released' because of interruption
                    } //Or check queue again because of timeout or spurious wake-up
                }
            }

            // Open printer if necessary
            if (print_os == null) {
                try {
                    print_os = getPrinterInterface();
                } catch (IOException e) {
                    // Notify failure and go for the next job
                    notifyError(task.callback, getLastPrinterError(), e);
                    continue;
                }
            }

            // Print current job here
            try {
                for (byte[] bbb : task.data) {
                    print_os.write(bbb);
                }
                notifySuccess(task.callback);
            } catch (IOException e) {
                notifyError(task.callback, getLastPrinterError(), e);
            }

            // Protection delay (ensure no printer input buffer overflow)
            try { Thread.sleep(BuildConfig.DEBUG ? 333 : 80); } catch (InterruptedException e) { }

        } // main thread cycle
    } //work()



    /*****  Open connection to the printer section (designed non thread-safe intentionally)  ******/
    private OutputStream getPrinterInterface() throws IOException {
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(btAdapter == null) {
            throw new IOException(lastPrinterError = context.getString(R.string.err_bt_not_supported));
        } else if (!btAdapter.isEnabled()) {
            lastPrinterError = context.getString(R.string.err_bt_not_enabled);
            throw new IOException("Bluetooth is not enabled");
        }

        BluetoothDevice printer = IOUtils.getBtDeviceByAddress(btAdapter, INNER_PRINTER_ADDRESS);
        if (printer == null) {
            throw new IOException(lastPrinterError = context.getString(R.string.err_bt_printer_not_found));
        }

        try {
            //The associated OutputStream will hold the reference for this socket and will close it
            // at the end. The socket is GC-protected
            BluetoothSocket socket = IOUtils.connectBtDeviceFrcomm(printer, PRINTER_SERVICE_CHANNEL_ID);
            return socket.getOutputStream();
        } catch (IOException e) {
            lastPrinterError = context.getString(R.string.err_bt_printer_connect);
            throw e;
        }
    }

    private String lastPrinterError = "";

    @Nullable
    private String getLastPrinterError() {
        return lastPrinterError;
    }



    /**********************************************************************************************/
    /***********************  Send result notifications to the UI thread  *************************/
    /***********************  This is Android-specific code  **************************************/
    /**********************************************************************************************/

    private void notifySuccess(@Nullable PrintResultCallback cb) {
        if (cb != null) {
            mUiHandler.obtainMessage(MSG_OK, cb).sendToTarget();
        }
    }

    private void notifyError(@Nullable PrintResultCallback cb, String reason, Exception ex) {
        if (cb != null) {
            Map<String, Object> map = new HashMap<>();
            map.put("cb", cb);
            map.put("reason", reason);
            map.put("ex", ex);
            mUiHandler.obtainMessage(MSG_ERR, map).sendToTarget();
        }
    }

    private static final int MSG_OK  = 0;
    private static final int MSG_ERR = -1;

    private final Handler mUiHandler = new Handler(Looper.getMainLooper()) {
        @SuppressWarnings("unchecked")
        @Override
        public void handleMessage(Message msg) {
            //This is executed on the UI thread.
            switch (msg.what) {
                case MSG_OK:
                    ((PrintResultCallback) msg.obj).onComplete();
                    break;
                case MSG_ERR:
                    Map<String, Object> map = (Map) msg.obj;
                    //noinspection ConstantConditions
                    ((PrintResultCallback) map.get("cb")).onError((String) map.get("reason"), (Exception) map.get("ex"));
                    break;
            }
        }
    };
}


/**************************************************************************************************/
class PrintTask {
    final Collection<byte[]> data;
    final PrintResultCallback callback;

    PrintTask(Collection<byte[]> data, PrintResultCallback callback) {
        this.data = data;
        this.callback = callback;
    }
}
