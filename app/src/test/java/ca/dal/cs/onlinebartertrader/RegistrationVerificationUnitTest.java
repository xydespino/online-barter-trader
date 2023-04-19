package ca.dal.cs.onlinebartertrader;

import org.junit.Test;

import ca.dal.cs.onlinebartertrader.LoginRegisterPages.RegistrationVerification;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RegistrationVerificationUnitTest {

    @Test
    public void isValidEmailCorrectFormatting() {
        assertTrue(RegistrationVerification.isValidEmail("username@gmail.com"));
        assertTrue(RegistrationVerification.isValidEmail("Hello@world.ca"));
        assertTrue(RegistrationVerification.isValidEmail("fake@email.org"));
    }

    @Test
    public void isValidEmailWrongFormatting() {
        assertFalse(RegistrationVerification.isValidEmail("username.com"));
        assertFalse(RegistrationVerification.isValidEmail("Hello@world"));
        assertFalse(RegistrationVerification.isValidEmail("@email.org"));
    }



    @Test
    public void isValidPassword() {
        assertTrue(RegistrationVerification.isValidPassword("Password123"));
        assertTrue(RegistrationVerification.isValidPassword("ASuperLongPassword"));
        assertFalse(RegistrationVerification.isValidPassword("shrtPas"));
        assertFalse(RegistrationVerification.isValidPassword("nocapitalshere"));
        assertFalse(RegistrationVerification.isValidPassword("ILOVETOYELL"));
    }
}
