package com.example.companyverification.service.client;

import com.example.companyverification.exception.ThirdPartyServiceException;
import com.example.companyverification.model.domain.Company;

import java.util.List;

public interface ThirdPartyClient {

    List<Company> search(String query) throws ThirdPartyServiceException;
}
