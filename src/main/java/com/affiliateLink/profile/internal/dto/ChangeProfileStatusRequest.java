package com.affiliateLink.profile.internal.dto;

import com.affiliateLink.profile.internal.entity.enumStatus.status;
import jakarta.validation.constraints.NotNull;

public class ChangeProfileStatusRequest {

    @NotNull
    private status newStatus;

    public status getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(status newStatus) {
        this.newStatus = newStatus;
    }
}