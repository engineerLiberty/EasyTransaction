package com.example.money_way.services;

import com.example.money_way.dto.request.CreateWalletRequest;
import com.example.money_way.dto.response.ApiResponse;
import com.example.money_way.dto.webhook.VerifyTransaction;
import com.example.money_way.dto.response.ViewWalletResponseDto;
import com.example.money_way.dto.webhook.WebHookResponse;
import org.springframework.http.ResponseEntity;

public interface WalletService {

    ApiResponse<ViewWalletResponseDto> viewBalance();
    ApiResponse createWallet(CreateWalletRequest request);

    ApiResponse<VerifyTransaction> verifyPayment(String trasactionId);

    ResponseEntity<String> processWebHookEvent(WebHookResponse<VerifyTransaction> webHookResponse);

}
