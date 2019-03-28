package ro.pub.cs.systems.eim.practicaltest01;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.sql.Date;
import java.util.Random;

public class ProcessingThread extends Thread {

    private double geometricMean;
    private double arithmMean;

    private Context context;
    private boolean isRunning = true;
    private Random random = new Random();

    public ProcessingThread(Context context, int firstNumber, int secondNumber){
        this.context = context;
        arithmMean = (firstNumber + secondNumber)/2;
        geometricMean = Math.sqrt(firstNumber * secondNumber);
    }

    @Override
    public void run() {
        Log.d("[ProcessingThread]", "Thread has started!");
        while (isRunning) {
            sendMessage();
            sleep();
        }
        Log.d("[ProcessingThread]", "Thread has stopped!");
    }

    private void sleep() {
        try {
            Thread.sleep(Constants.SLEEP_TIME);
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    private void sendMessage() {
        Intent intent = new Intent();
        intent.setAction(Constants.actionTypes[random.nextInt(Constants.actionTypes.length)]);
        intent.putExtra("message", new Date(System.currentTimeMillis()) + " " + arithmMean + " " + geometricMean);
        intent.setAction(Constants.ACTION_STRING);
        context.sendBroadcast(intent);
    }

    public void stopThread() {
        isRunning = false;
    }
}
