package com.academicsaas.reporting.application.usecase;

import com.academicsaas.reporting.application.port.OverviewRepository;
import com.academicsaas.reporting.domain.model.InstitutionalOverview;
import org.springframework.stereotype.Service;

@Service
public class GetInstitutionalOverviewUseCase {
    private final OverviewRepository overviewRepository;
    public GetInstitutionalOverviewUseCase(OverviewRepository overviewRepository) {
        this.overviewRepository = overviewRepository;
    }
    public InstitutionalOverview execute() {
        return overviewRepository.getInstitutionalOverview();
    }
}
