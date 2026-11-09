package com.affiliateLink.profile.internal.entity;

import com.affiliateLink.profile.internal.entity.enumStatus.status;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name =  "profile_profiles")
@Inheritance(strategy = InheritanceType.JOINED)


public abstract class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email" , nullable = false , unique = true, length = 255)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status" , nullable = false , length = 20)
    private status statu = status.PENDING;

    @Column(name = "created_at" , nullable = false , updatable = false)
    private LocalDateTime createdAt;

    protected Profile(){

    }

    @PrePersist
    protected void onCreate() {
        if (statu == null) {
            statu = status.PENDING;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public status getStatu() {
        return statu;
    }

    public void setStatu(status statu) {
        this.statu = statu;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
