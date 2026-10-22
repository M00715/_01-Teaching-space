package learn.springboot.common;

import lombok.Getter;

@Getter
public class BizException extends RuntimeException {
    private final BizCode bizCode;

    public BizException(BizCode bizCode) {
        super(bizCode.getMsg());
        this.bizCode = bizCode;
    }

}
