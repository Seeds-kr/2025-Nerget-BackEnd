// src/main/java/com/seeds/NergetBackend/exception/DuplicateNicknameException.java
package com.seeds.NergetBackend.exception;

public class DuplicateNicknameException extends RuntimeException {
    public DuplicateNicknameException(String nickname) {
        super("이미 사용 중인 닉네임: " + nickname);
    }
}
