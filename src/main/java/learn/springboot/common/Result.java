package learn.springboot.common;

import lombok.Data;

@Data
public class Result<T> {
    private  Integer code;
    private String msg;
    private T data;

    public static  <T> Result<T> success(T data){
        Result<T> r = new Result<>();
        r.code = 200;
        r.msg = "success";
        r.data = data;
        return r;
    }
    public static  <T> Result<T> error(Integer code, String msg){
        Result<T> r = new Result<>();
        r.code = code;
        r.msg = msg;
        return r;
    }
    // 重载
    public static <T> Result<T> error(BizCode bizCode){
        Result<T> r = new Result<>();
        r.code = bizCode.getCode();
        r.msg = bizCode.getMsg();
        return r;
    }
}
