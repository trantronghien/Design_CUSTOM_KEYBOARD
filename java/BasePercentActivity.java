import android.app.Activity;
import android.inputmethodservice.KeyboardView;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
public abstract class BasePercentActivity extends AppCompatActivity {

private EditText beforeEditText;
private EditText percentEditText;
private TextView beforeTitle;
private TextView percentTitle;
private TextView afterTitle;

    
protected KeyboardView keyboardView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResourceId());
        
        beforeTitle = (TextView)findViewById(R.id.before_label);
        percentTitle = (TextView)findViewById(R.id.percent_label);
        afterTitle = (TextView)findViewById(R.id.after_label);
        
        percentResult = (TextView)findViewById(R.id.percent_result);
        
        keyboardView = (KeyboardView)findViewById(R.id.keyboard_view);
        keyboardView.setPreviewEnabled(false);
        keyboard = new Keyboard(PercentDiscountActivity.this, R.xml.keyboards);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(keyboardActionListener);
      
        registerEditText(R.id.before_input);
        registerEditText(R.id.percentage_input);

   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
    public KeyboardView.OnKeyboardActionListener keyboardActionListener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) { }
        @Override
        public void onRelease(int primaryCode) { }
        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            long eventTime = System.currentTimeMillis();
            KeyEvent event = new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, primaryCode, 0, 0, 0, 0, KeyEvent.FLAG_SOFT_KEYBOARD | KeyEvent.FLAG_KEEP_TOUCH_MODE);
            dispatchKeyEvent(event);
            if(primaryCode == KeyEvent.KEYCODE_NUMPAD_EQUALS){
                displayCalculatedResult();
            }
        }
        @Override
        public void onText(CharSequence text) { }
        @Override
        public void swipeLeft() { }
        @Override
        public void swipeRight() { }
        @Override
        public void swipeDown() { }
        @Override
        public void swipeUp() { }
    };
    protected abstract int getLayoutResourceId();
    protected abstract void displayCalculatedResult();
    public void registerEditText(int resid) {
        // Find the EditText 'res_id'
        EditText edittext = (EditText) findViewById(resid);
        edittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                CharSequence mS = editable.subSequence(0, editable.length());
                if (!mS.toString().equals("") || mS.toString() != null) {
                    if (editable.length() > 0 && mS.toString().contains("=")) {
                        editable.replace(editable.length() - 1, editable.length(), "");
                    }
                }
            }
        });
        // Make the custom keyboard appear
        edittext.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) showCustomKeyboard(v);
                else hideCustomKeyboard();
            }
        });
        edittext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCustomKeyboard(v);
            }
        });
        edittext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                EditText edittext = (EditText) v;
                int inType = edittext.getInputType();       // Backup the input type
                edittext.setInputType(InputType.TYPE_NULL); // Disable standard keyboard
                edittext.onTouchEvent(event);               // Call native handler
                edittext.setInputType(inType);              // Restore input type
                return true; // Consume touch event
            }
        });
    }
    public void hideCustomKeyboard() {
        keyboardView.setVisibility(View.GONE);
        keyboardView.setEnabled(false);
    }
    public void showCustomKeyboard( View v) {
        keyboardView.setVisibility(View.VISIBLE);
        keyboardView.setEnabled(true);
        if( v!=null ){
            ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }
    public boolean isCustomKeyboardVisible() {
        return keyboardView.getVisibility() == View.VISIBLE;
    }
    @Override public void onBackPressed() {
        if( isCustomKeyboardVisible() ) hideCustomKeyboard(); else this.finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base_percent, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public String removeTrailingZero(String formattingInput){
        if(!formattingInput.contains(".")){
            return formattingInput;
        }
        int dotPosition = formattingInput.indexOf(".");
        String newValue = formattingInput.substring(dotPosition, formattingInput.length());
        if(newValue.startsWith(".0")){
            return formattingInput.substring(0, dotPosition);
        }
        return formattingInput;
    }
}