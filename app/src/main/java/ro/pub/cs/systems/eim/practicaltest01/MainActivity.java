package ro.pub.cs.systems.eim.practicaltest01;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText leftEditText = null;
    private EditText rightEditText = null;
    private Button leftButton = null;
    private Button rightButton = null;
    private Button navigateButton = null;
    private IntentFilter intentFilter = new IntentFilter();
    private int serviceStatus = Constants.SERVICE_STOPPED;
    private final static int SECONDARY_ACTIVITY_REQUEST_CODE = 1;

    private void checkService(){
        int leftNumberOfClicks = Integer.parseInt(leftEditText.getText().toString());
        int rightNumberOfClicks = Integer.parseInt(rightEditText.getText().toString());

        if (leftNumberOfClicks + rightNumberOfClicks > Constants.NUMBER_OF_CLICKS_THRESHOLD
                && serviceStatus == Constants.SERVICE_STOPPED) {
            Intent intent = new Intent(getApplicationContext(), PracticalTest01Service.class);
            intent.putExtra("firstNumber", leftNumberOfClicks);
            intent.putExtra("secondNumber", rightNumberOfClicks);
            getApplicationContext().startService(intent);
            serviceStatus = Constants.SERVICE_STARTED;
        }
    }

    private LeftButtonClickListener leftButtonClickListener = new LeftButtonClickListener();
    private class LeftButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Integer left = Integer.valueOf(leftEditText.getText().toString());
            left++;
            leftEditText.setText(left.toString());
            checkService();
        }
    }

    private RightButtonClickListener rightButtonClickListener = new RightButtonClickListener();
    private class RightButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Integer right = Integer.valueOf(rightEditText.getText().toString());
            right++;
            rightEditText.setText(right.toString());
            checkService();
        }
    }

    private NavigateButtonClickListener navigateButtonClickListener = new NavigateButtonClickListener();
    private class NavigateButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(getApplicationContext(), SecondaryActivity.class);
            int numberOfClicks = Integer.parseInt(leftEditText.getText().toString()) + Integer.parseInt(rightEditText.getText().toString());
            intent.putExtra("numberOfClicks", numberOfClicks);
            startActivityForResult(intent, SECONDARY_ACTIVITY_REQUEST_CODE);
            checkService();
        }
    }

    private MessageBroadcastReceiver messageBroadcastReceiver = new MessageBroadcastReceiver();
    private class MessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("[Message]", intent.getStringExtra("message"));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        leftButton = (Button)findViewById(R.id.left_button);
        rightButton = (Button)findViewById(R.id.right_button);
        leftEditText = (EditText)findViewById(R.id.left_edit_text);
        rightEditText = (EditText)findViewById(R.id.right_edit_text);
        navigateButton = (Button) findViewById(R.id.navigate_button);

        leftEditText.setText(String.valueOf(0));
        rightEditText.setText(String.valueOf(0));
        leftButton.setOnClickListener(leftButtonClickListener);
        rightButton.setOnClickListener(rightButtonClickListener);
        navigateButton.setOnClickListener(navigateButtonClickListener);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("leftString")) {
                leftEditText.setText(savedInstanceState.getString("leftString"));
            } else {
                leftEditText.setText(String.valueOf(0));
            }
            if (savedInstanceState.containsKey("rightString")) {
                rightEditText.setText(savedInstanceState.getString("rightString"));
            } else {
                rightEditText.setText(String.valueOf(0));
            }
        } else {
            leftEditText.setText(String.valueOf(0));
            rightEditText.setText(String.valueOf(0));
        }

        for (int index = 0; index < Constants.actionTypes.length; index++) {
            intentFilter.addAction(Constants.actionTypes[index]);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("leftString", leftEditText.getText().toString());
        savedInstanceState.putString("rightString", rightEditText.getText().toString());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState.containsKey("leftString")) {
            leftEditText.setText(savedInstanceState.getString("leftString"));
        } else {
            leftEditText.setText(String.valueOf(0));
        }
        if (savedInstanceState.containsKey("rightString")) {
            rightEditText.setText(savedInstanceState.getString("rightString"));
        } else {
            rightEditText.setText(String.valueOf(0));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == SECONDARY_ACTIVITY_REQUEST_CODE) {
            Toast.makeText(this, "The activity returned with result " + resultCode, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        Intent intent = new Intent(this, PracticalTest01Service.class);
        stopService(intent);
        serviceStatus = Constants.SERVICE_STOPPED;
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(messageBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(messageBroadcastReceiver);
        super.onPause();
    }
}
