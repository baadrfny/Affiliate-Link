package com.affiliateLink.profile.entity;


import com.affiliateLink.profile.entity.enumStatus.Niche;
import jakarta.persistence.*;


@Entity
@Table(name = "profile_publishers")


public class Publisher extends Profile {
    @Column(name = "first_name" , nullable = false , length = 100)
    private String firstName;

    @Column(name = "last_name" , nullable = false , length = 100)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "niche" , nullable = false )
    private Niche niche;

    @Column(name = "bio" , nullable = true )
    private String bio;

    @Column(name = "country" , nullable = true, length = 100)
    private String country;


    public Publisher() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Niche getNiche() {
        return niche;
    }

    public void setNiche(Niche niche) {
        this.niche = niche;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
