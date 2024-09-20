package com.identity_user.identtity_user.service;

import com.identity_user.identtity_user.dto.request.AuthenticationRequest;
import com.identity_user.identtity_user.dto.request.IntrospectRequest;
import com.identity_user.identtity_user.dto.request.LogoutRequest;
import com.identity_user.identtity_user.dto.request.RefreshRequest;
import com.identity_user.identtity_user.dto.response.AuthenticationResponse;
import com.identity_user.identtity_user.dto.response.IntrospectResponse;
import com.identity_user.identtity_user.entity.InvalidatedToken;
import com.identity_user.identtity_user.entity.User;
import com.identity_user.identtity_user.exception.AppException;
import com.identity_user.identtity_user.exception.ErrorCode;
import com.identity_user.identtity_user.repository.InvalidatedTokenRepository;
import com.identity_user.identtity_user.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimNames;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.sql.Ref;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected  String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected  long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected  long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token,false);
        } catch (AppException e) {
            isValid= false;
        }

        return IntrospectResponse.builder().valid(isValid).build();
    }
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
       try {
           var signToken = verifyToken(request.getToken(),false);
           var jit = signToken.getJWTClaimsSet().getJWTID();
           Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
           InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
           invalidatedTokenRepository.save(invalidatedToken);
       } catch (AppException e) {
           log.info("token already expired");
       }

    }
    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signJwt = verifyToken(request.getToken(), true);
        var jit = signJwt.getJWTClaimsSet().getJWTID();
        var expiryTime = signJwt.getJWTClaimsSet().getExpirationTime();
         InvalidatedToken invalidatedToken = InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
         invalidatedTokenRepository.save(invalidatedToken);
         var username = signJwt.getJWTClaimsSet().getSubject();
        var user = userRepository.findByUserName(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_UNAUTHENTICATED)
        );
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }
    public SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh) ? new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION,ChronoUnit.SECONDS).toEpochMilli())

        :signedJWT.getJWTClaimsSet().getExpirationTime();


        var verified =signedJWT.verify(verifier);
        if (!(verified && expiryTime.after(new Date()))) {
            throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
        }
        if (invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
        }
    return signedJWT;
    }
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUserName(request.getUsername()).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if (!authenticated) {
            throw new AppException(ErrorCode.USER_UNAUTHENTICATED);
        }
        var token = generateToken(user);
        return AuthenticationResponse.builder().token(token).authenticated(true).build();
    }
    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder().subject(user.getUserName()).issuer("kun.com").
                issueTime(new Date()).expirationTime(new Date(Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli())).jwtID(UUID.randomUUID().toString()).claim("scope", buildScope(user)).build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header,payload);
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Cannot create token", e);
            throw new RuntimeException(e);
        }
    }
    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> {
                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions()
                            .forEach(permission -> stringJoiner.add(permission.getName()));
            });

        return stringJoiner.toString();
    }
}
