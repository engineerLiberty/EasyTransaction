package com.example.money_way.service.impl;

import com.example.money_way.dto.request.TvPurchaseRequest;
import com.example.money_way.dto.response.ApiResponse;
import com.example.money_way.dto.response.TvPurchaseResponse;
import com.example.money_way.exception.ResourceNotFoundException;
import com.example.money_way.model.Beneficiary;
import com.example.money_way.model.Transaction;
import com.example.money_way.model.User;
import com.example.money_way.model.Wallet;
import com.example.money_way.repository.BeneficiaryRepository;
import com.example.money_way.repository.TransactionRepository;
import com.example.money_way.repository.WalletRepository;
import com.example.money_way.service.BillService;
import com.example.money_way.utils.AppUtil;
import com.example.money_way.utils.EnvironmentVariables;
import com.example.money_way.utils.RestTemplateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BillServiceImpl implements BillService {
    private final RestTemplate restTemplate;
    private final EnvironmentVariables environmentVariables;
    private final BeneficiaryRepository beneficiaryRepository;

    private final AppUtil appUtil;
    private final TransactionRepository transactionRepository;
    private final WalletRepository walletRepository;

    private final RestTemplateUtil restTemplateUtil;


    @Override
    public ApiResponse<TvPurchaseResponse> purchaseTvSubscription(TvPurchaseRequest request) {
        String transactionReference = appUtil.getReference() + "TV-SUBSCRIPTION";
        request.setRequest_id(transactionReference);
        User user = appUtil.getLoggedInUser();
        Long userId = user.getId();
        Wallet wallet = walletRepository.findByUserId(userId).orElseThrow(() -> new ResourceNotFoundException("Wallet Not Found"));
        BigDecimal walletBalance = wallet.getBalance();
            if (walletBalance.compareTo(request.getAmount()) >= 0) {

               HttpHeaders headers = restTemplateUtil.getVTPASS_Header();

            HttpEntity<TvPurchaseRequest> entity = new HttpEntity<>(request, headers);
            TvPurchaseResponse tvPurchaseResponse = restTemplate.exchange(environmentVariables.getPurchaseSubscriptionUrl(),
                    HttpMethod.POST, entity, TvPurchaseResponse.class).getBody();
            if (request.isSaveBeneficiary()) {
                saveBeneficiary(request, userId);
            }
            wallet.setBalance(BigDecimal.valueOf(walletBalance.doubleValue() - request.getAmount().doubleValue()));
            walletRepository.save(wallet);
            saveTransaction(request,transactionReference,userId);
            return new ApiResponse<>("Success", "Successful Transaction", tvPurchaseResponse);
        }
        return new ApiResponse<>("Failed","Insufficient Wallet Balance",null);
    }

    private void saveBeneficiary(TvPurchaseRequest request, Long userId) {
        Optional<Beneficiary> savedBeneficiary = beneficiaryRepository.findBeneficiariesByPhoneNumberAndUserId(request.getPhone(), userId);
        if (savedBeneficiary.isEmpty()) {
            Beneficiary beneficiary = Beneficiary.builder()
                    .userId(userId)
                    .phoneNumber(request.getPhone()).build();
            beneficiaryRepository.save(beneficiary);
        }
    }

    private void saveTransaction(TvPurchaseRequest request, String transactionReference, Long userId) {
        Transaction transaction = Transaction.builder()
                .userId(userId).currency("NIL")
                .request_id(transactionReference)
                .amount(request.getAmount()).build();
                transactionRepository.save(transaction);
     }
}
