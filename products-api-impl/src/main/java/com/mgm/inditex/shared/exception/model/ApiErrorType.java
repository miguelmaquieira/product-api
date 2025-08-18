package com.mgm.inditex.shared.exception.model;

import lombok.Getter;

/**
 * Defines various types of errors that can occur within the API.
 *
 * @author Miguel Maquieira
 */
@Getter
public enum ApiErrorType
{
    GENERIC( 1000 ),
    VALIDATION( 1001 ),
    NOT_FOUND( 1002 ),
    DUPLICATED( 1003 );

    private final int code; // Field to store the integer code for each error type

    /**
     * Private constructor to initialize the code for each enum constant.
     *
     * @param code The integer code associated with the error type.
     */
    ApiErrorType( final int code )
    {
        this.code = code;
    }
}
