//Gerekli kütüphane importlari, Properties'den itibaren gelenler mail atmak için.
import java.io.*;
import java.util.*;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class Main {
    //Sonuçların yazılacağı txt dosyasının adını sabitliyorum.
    private static final String DOSYA_ADI = "uyeler.txt";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in); //Klavye input işlemleri
        // Üyeleri Arraylistlerde tutuyorum.
        List<ElitUye> elitUyeler = new ArrayList<>();
        List<GenelUye> genelUyeler = new ArrayList<>();

        while (true) {
            System.out.println("Menü:");
            System.out.println("1- Elit üye ekleme");
            System.out.println("2- Genel üye ekleme");
            System.out.println("3- Mail gönderme");
            System.out.print("Seçiminizi yapın: ");
            int secim = sc.nextInt();
            sc.nextLine();
            //İlk menüdeki seçime göre ilgili üye tipinde yaratım için bilgileri istiyorum.
            if (secim == 1 || secim == 2) {
                System.out.println("Bilgileri arada TAB olacak şekilde giriniz:");
                String bilgiler = sc.nextLine();
                //Bilgileri stringten split metodu sayesinde tokenlere ayırarak arraye dolduruyorum
                String[] bilgilerTokens = bilgiler.split("\t");
                String isim = bilgilerTokens[0];
                String soyisim = bilgilerTokens[1];
                String email = bilgilerTokens[2];
                //Yazım için dosya işlemlerini hata nedeniyle crush olmaması adına try-catch blogunda yazıyorum
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(DOSYA_ADI, true))) {
                    if (secim == 1) {
                        //Elit üye yaratma, listeye ekleme ve dosyaya yazma
                        elitUyeler.add(new ElitUye(isim, soyisim, email));
                        bw.write("#ELIT UYE:\n");
                        bw.write(isim + "\t" + soyisim + "\t" + email + "\n");
                    } else {
                        //Genel üye yaratma, listeye ekleme ve dosyaya yazma
                        genelUyeler.add(new GenelUye(isim, soyisim, email));
                        bw.write("#GENEL UYE:\n");
                        bw.write(isim + "\t" + soyisim + "\t" + email + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (secim == 3) { //Mail atma menüsü
                System.out.println("Mail menüsü:");
                System.out.println("1- Elit üyelere mail");
                System.out.println("2- Genel üyelere mail");
                System.out.println("3- Tüm üyelere mail");
                System.out.print("Seçiminizi yapın: ");
                int mailSecim = sc.nextInt();
                sc.nextLine();
                System.out.print("Mail içeriği: ");
                String mailIcerik = sc.nextLine(); //Mail içeriğini dolduruyorum

                //Elit üyelere mail atıyorum
                if (mailSecim == 1 || mailSecim == 3) {
                    System.out.println("Elit üyelere mail gönderiliyor...");
                    for (ElitUye uye : elitUyeler) {
                        uye.mailBildirim(mailIcerik);
                        SendMail(uye.email,mailIcerik);
                    }
                }
                //Genel üyelere mail atıyorum
                if (mailSecim == 2 || mailSecim == 3) {
                    System.out.println("Genel üyelere mail gönderiliyor...");
                    for (GenelUye uye : genelUyeler) {
                        uye.mailBildirim(mailIcerik);
                        SendMail(uye.email,mailIcerik);
                    }
                }
            }
        }
    }
    //Mail atma metodum. JavaMail api'den yararlanıyorum
    public static void SendMail(String to, String body) {
        String from = "";   //GONDERICI TEST HESABI MAIL ADRESI EKLENECEK
        String password = "";   //GONDERICI TEST HESABINIIN SIFRESI EKLENECEK

        //Propertyleri dolduruyorum
        Properties props = System.getProperties();
        props.put("mail.smtp.user", "username");
        //BURALAR (HOST, PORT, SMPT vb. TEST MAILI GMAIL DEGILSE DEGISMELI)
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "25");
        props.put("mail.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.EnableSSL.enable", "true");
        props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.setProperty("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.port", "465");
        props.setProperty("mail.smtp.socketFactory.port", "465");

        //Mail Authentication metodu
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try { //Maili atıyorum
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Ornek Mail Konu");
            message.setText(body);
            Transport.send(message);

        } catch (MessagingException mex) {

        }
    }
}
// abstract üye sınıfım. buradan obje üretmediğim için abstract.
abstract class Uye {
    protected String isim;
    protected String soyisim;
    protected String email;

    public Uye(String isim, String soyisim, String email) {
        this.isim = isim;
        this.soyisim = soyisim;
        this.email = email;
    }

    public abstract void mailBildirim(String mesaj);
}

//Elit ve genel üyelerim Uye sınıfını extend ediyor.
class ElitUye extends Uye {
    public ElitUye(String isim, String soyisim, String email) {
        super(isim, soyisim, email);
    }

    @Override
    public void mailBildirim(String mesaj) {
        System.out.println("Elit üye " + isim + " " + soyisim + " (" + email + ") için mail gönderildi: " + mesaj);
    }
}

class GenelUye extends Uye {
    public GenelUye(String isim, String soyisim, String email) {
        super(isim, soyisim, email);
    }

    @Override
    public void mailBildirim(String mesaj) {
        System.out.println("Genel üye " + isim + " " + soyisim + " (" + email + ") için mail gönderildi: " + mesaj);
    }
}