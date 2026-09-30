package io.github.hectorvent.floci.services.iam;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.matchesPattern;

/**
 * Integration tests for the account's outbound web identity federation toggle. The toggle is one
 * per-account value, so the cases run in order and share it.
 */
@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class IamOutboundWebIdentityFederationIntegrationTest {

    private static final String IAM_CREDENTIAL =
            "AWS4-HMAC-SHA256 Credential=test/20260227/us-east-1/iam/aws4_request";
    private static final String ISSUER = "https://[0-9a-f-]{36}\\.tokens\\.sts\\.global\\.api\\.aws";

    private io.restassured.response.ValidatableResponse call(String action) {
        return given()
            .formParam("Action", action)
            .header("Authorization", IAM_CREDENTIAL)
        .when()
            .post("/")
        .then();
    }

    @Test
    @Order(1)
    void getBeforeEnableIsFeatureDisabled() {
        call("GetOutboundWebIdentityFederationInfo")
            .statusCode(409)
            .body("ErrorResponse.Error.Code", equalTo("FeatureDisabled"));
    }

    @Test
    @Order(2)
    void enableReturnsTheIssuer() {
        call("EnableOutboundWebIdentityFederation")
            .statusCode(200)
            .body("EnableOutboundWebIdentityFederationResponse.EnableOutboundWebIdentityFederationResult.IssuerIdentifier",
                    matchesPattern(ISSUER));
    }

    @Test
    @Order(3)
    void secondEnableIsFeatureEnabled() {
        call("EnableOutboundWebIdentityFederation")
            .statusCode(409)
            .body("ErrorResponse.Error.Code", equalTo("FeatureEnabled"));
    }

    @Test
    @Order(4)
    void getReturnsTheIssuer() {
        call("GetOutboundWebIdentityFederationInfo")
            .statusCode(200)
            .body("GetOutboundWebIdentityFederationInfoResponse.GetOutboundWebIdentityFederationInfoResult.IssuerIdentifier",
                    matchesPattern(ISSUER))
            .body("GetOutboundWebIdentityFederationInfoResponse.GetOutboundWebIdentityFederationInfoResult.JwtVendingEnabled",
                    equalTo("true"));
    }

    @Test
    @Order(5)
    void disableTurnsItOff() {
        call("DisableOutboundWebIdentityFederation").statusCode(200);
        call("GetOutboundWebIdentityFederationInfo").statusCode(409);
        call("DisableOutboundWebIdentityFederation")
            .statusCode(409)
            .body("ErrorResponse.Error.Code", equalTo("FeatureDisabled"));
    }
}
