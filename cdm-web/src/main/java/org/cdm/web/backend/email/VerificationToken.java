package org.cdm.web.backend.email;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.cdm.web.backend.role.Role;
import org.cdm.web.backend.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

@Getter
@Setter
@Entity
public class VerificationToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String token;


    //private String userName;
    //@OneToOne(targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    //@ManyToOne (targetEntity = User.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    //@JoinColumn(name = "id")
    //@ManyToOne
    //@JoinColumn(name = "id")
    //private User user;

    private String username;

    private String userpass;

    private Date expiryDate;

    public VerificationToken() {
        super();
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public VerificationToken(final String token, final User user) {
        super();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        this.token = token;
        //this.user = user;
        this.username = user.getUsername();
        this.userpass = user.getPassword();
        this.userpass = bCryptPasswordEncoder.encode(this.userpass);
        this.expiryDate = calculateExpiryDate(EXPIRATION);
        /*
        User userToAdd = user;
        userToAdd.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        userToAdd.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        this.user = userToAdd;

         */
    }

    public User getUser() {
        return new User(this.username, this.userpass);
    }
}
