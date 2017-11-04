package utilities;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.*;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class CaCertBuilder {

    private final Logger LOGGER = LoggerFactory.getLogger(CaCertBuilder.class);

    private final SecureRandom secureRandom = new SecureRandom();

    public CaCertBuilder() {
    }

    public X509Certificate buildCaCert() throws NoSuchAlgorithmException, CertIOException, OperatorCreationException, CertificateException {
        Instant now = (new Date()).toInstant();
        Date notBefore = Date.from(now);             // time from which certificate is valid
        Date notAfter = Date.from(now.plus(365, ChronoUnit.DAYS)); // time after which certificate is not valid
        BigInteger serialNumber = BigInteger.valueOf(Math.abs(secureRandom.nextInt()));
        ;       // serial number for certificate
        X500Name name = buildName();
        X500Name issuer = name;
        KeyPair keyPair = createKeyPair();               // public/private key pair that we are creating certificate for
        PublicKey publicKey = keyPair.getPublic();              // private key of the certifying authority (ca) certificate
        PrivateKey privateKey = keyPair.getPrivate();
        SubjectKeyIdentifier subjectKeyIdentifier = new SubjectKeyIdentifier(publicKey.getEncoded());
        AuthorityKeyIdentifier authorityKeyIdentifier = new AuthorityKeyIdentifier(publicKey.getEncoded());
        KeyUsage usage = new KeyUsage(KeyUsage.keyCertSign
                | KeyUsage.digitalSignature | KeyUsage.keyEncipherment
                | KeyUsage.dataEncipherment | KeyUsage.cRLSign);
        ASN1EncodableVector purposes = new ASN1EncodableVector();
        purposes.add(KeyPurposeId.id_kp_serverAuth);
        purposes.add(KeyPurposeId.id_kp_clientAuth);
        purposes.add(KeyPurposeId.anyExtendedKeyUsage);

        X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(name, serialNumber, notBefore, notAfter, issuer, publicKey);
        certBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(true));
        certBuilder.addExtension(Extension.subjectKeyIdentifier, false, subjectKeyIdentifier);
        certBuilder.addExtension(Extension.keyUsage, false, usage);
        certBuilder.addExtension(Extension.extendedKeyUsage, false, new DERSequence(purposes));

        ContentSigner signer = new JcaContentSignerBuilder("SHA256WithRSAEncryption").build(privateKey);

        X509CertificateHolder certHolder = certBuilder.build(signer);
        JcaX509CertificateConverter certConverter = new JcaX509CertificateConverter();
        return certConverter.getCertificate(certHolder);
    }

    private KeyPair createKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen;
        try {
            keyGen = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("Error occured generating Key Pair -- {}", e.getMessage());
            throw e;
        }
        keyGen.initialize(2048);
        return keyGen.generateKeyPair();
    }

    private X500Name buildName() {
        X500NameBuilder nameBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        nameBuilder.addRDN(BCStyle.CN, "TestCA");
        nameBuilder.addRDN(BCStyle.O, "TestOrganization");
        nameBuilder.addRDN(BCStyle.OU, "TestUnit");

        return nameBuilder.build();
    }
}
