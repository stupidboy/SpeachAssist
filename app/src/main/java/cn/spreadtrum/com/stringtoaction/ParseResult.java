package cn.spreadtrum.com.stringtoaction;

/**
 * Created by SPREADTRUM\joe.yu on 4/5/16.
 */
public class ParseResult {
    static final int RES_NOT_REC = 0;
    static final int RES_OK = 1;
    static final int RES_OP_FAILED = -1;

    public int resultCode;
    public String returnString;

    public ParseResult() {

    }
    public ParseResult(int code) {
        resultCode = code;
    }
    public ParseResult(int code ,String string){
        resultCode = code;
        returnString = string;
    }
    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getReturnString() {
        return returnString;
    }

    public void setReturnString(String returnString) {
        this.returnString = returnString;
    }
}
