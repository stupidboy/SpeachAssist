package cn.spreadtrum.com.stringtoaction;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.widget.Toast;
import com.android.i18n.phonenumbers.*;

import java.util.Iterator;
import java.util.Locale;

/**
 * Created by SPREADTRUM\joe.yu on 3/21/16.
 */
public class CallAction extends BaseAction {

    public CallAction(Context mContext) {
        super(mContext);
    }

    @Override
    protected void init() {
        mPossStrings = mContext.getResources().getStringArray(R.array.callactions);
    }

    @Override
    protected ParseResult runAction(String src, String key) {
        String number = handleCallAction(src,key);
        if(!number.equals("")) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
            return new ParseResult(ParseResult.RES_OK);
        }
        return new ParseResult(ParseResult.RES_OP_FAILED,mContext.getString(R.string.parse_result_op_failed_number_not_rec));
    }

    String handleCallAction(String src, String key){
        // 1. dial number:
        String number = "";
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Iterable<PhoneNumberMatch> matches = phoneNumberUtil.findNumbers(src, Locale.getDefault().getCountry(), PhoneNumberUtil.Leniency.POSSIBLE,Long.MAX_VALUE);
        for(PhoneNumberMatch match : matches){
            //Toast.makeText(mContext,PhoneNumberUtils.normalizeNumber(match.rawString()),Toast.LENGTH_SHORT).show();
            number = PhoneNumberUtils.normalizeNumber(match.rawString());
            break;
        }
        //2 . dial to someone
        Log.e("joe","number --->"+number);
        //Toast.makeText(mContext, "call---->" +number,Toast.LENGTH_SHORT).show();

        return number;
    }
    @Override
    protected String getName() {
        return super.getName();
    }
}
