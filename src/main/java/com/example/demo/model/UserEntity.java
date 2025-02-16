package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = "username")})
public class UserEntity {

    @Id
    @GeneratedValue(generator="system-uuid")
    @GenericGenerator(name="system-uuid", strategy = "uuid")
    private String id; // 유저에게 고유하게 부여되는 id.

    @Column(nullable = false)
    private String username; // 아이디로 사용할 유저네임. 이메일이거나 그냥 문자열일수도 있음.

    private String password; // 암호
    private String role; // 사용자 역할 ag. admin, normal
    private String authProvider; // OAuth에서 사용할, 유저정보제공자(github, naver, google등)
}
