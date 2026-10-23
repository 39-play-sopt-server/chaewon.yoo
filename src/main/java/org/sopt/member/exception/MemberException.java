package org.sopt.member.exception;

import org.sopt.global.exception.BusinessException;
import org.sopt.member.code.MemberErrorCode;

public class MemberException extends BusinessException {
    public MemberException(MemberErrorCode errorCode) {
        super(errorCode);
    }
}
