package com.example.money_way.services;

import com.example.money_way.dto.response.ApiResponse;
import com.example.money_way.dto.response.ViewWalletResponseDto;

public interface WalletService {

    ApiResponse<ViewWalletResponseDto> viewBalance();


}
