package com.affiliateLink.profile.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "profile_advertisers")
public class Advertiser extends Profile {


    @Column(name = "company_name" , nullable = false , length = 255)
    private String companyName;

    @Column(name = "sector" , nullable = false , length = 100)
    private String sector;

    @Column(name = "website" , nullable = true , length = 255)
    private String website;





}
