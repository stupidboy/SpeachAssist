package cn.spreadtrum.com.stringtoaction;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.spreadtrum.com.stringtoaction.view.FloatView;
import cn.spreadtrum.com.stringtoaction.view.FloatViewManager;


public class MainActivity extends Activity {
    private ListView mList;
    private Button mButton;
    private TextToSpeech tts ;
    private ActionParser mParser ;
    private FloatViewManager mViewManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mButton = (Button)this.findViewById(R.id.button);
        mViewManager = new FloatViewManager(this);
        PackageManager pm = this.getPackageManager();
        List<ResolveInfo> infos = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH),0);
        mParser = new ActionParser(this.getApplicationContext());
        tts = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    tts.setLanguage(MainActivity.this.getResources().getConfiguration().locale);
                }
            }
        });
        if(infos.size() != 0){
            mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startVoiceRecognitionActivity();
                }
            });

        }
    }
    private void startVoiceRecognitionActivity()

    {

        // 通过Intent传递语音识别的模式

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        // 语言模式和自由形式的语音识别

        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // 提示语音开始

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "请说话.");

        // 开始执行我们的Intent、语音识别

        startActivityForResult(intent, 1234);

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)

    {

        if (requestCode == 1234
                && resultCode == RESULT_OK)

        {

            // 取得语音的字符

            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            //mList.setAdapter(new ArrayAdapter<String>(this,
            //        android.R.layout.simple_list_item_1, matches));
            //Toast.makeText(this,matches.get(0),Toast.LENGTH_SHORT).show();
            //mViewManager.showText(matches.get(0));
            ParseResult ret = mParser.Parse(matches.get(0));
            if(ret.getReturnString() != null){
                tts.speak(ret.getReturnString(), TextToSpeech.QUEUE_FLUSH, null);
                //mViewManager.showText(ret.getReturnString());
            }

        }

        super.onActivityResult(requestCode, resultCode, data);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
