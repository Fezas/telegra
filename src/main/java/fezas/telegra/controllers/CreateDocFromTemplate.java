/*
 * Copyright (c) 2021. Stepantsov P.V.
 */

package fezas.telegra.controllers;

//import com.itextpdf.html2pdf.ConverterProperties;
//import com.itextpdf.html2pdf.HtmlConverter;
//import com.itextpdf.html2pdf.resolver.font.DefaultFontProvider;

import com.google.zxing.WriterException;
import fezas.telegra.dao.*;
import fezas.telegra.entity.*;
import fezas.telegra.util.QrCodeCreateUtil;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.images.FileImageProvider;
import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CreateDocFromTemplate {
    private static final Logger logger = LogManager.getLogger();
    private PropertyAppDAO propertyAppDAO = PropertyAppDAO.getInstance();
    private User user = UserLoginController.getInstance().getCurrentUser();
    private Optional<PropertyApp> propertyApp = propertyAppDAO.findById(user.getUserId());

    private Telegramma tlg;
    private List<Mailing> mailings;
    private List<Type> typeInTlgs;
    private String paragraphs;

    public CreateDocFromTemplate(Telegramma tlg) {
        this.tlg = tlg;
    }

    private void initalizeTagValue() {
        //пункты перечня
        List<ParagraphEntity> parslist = ParsInTlgsDAO.getInstance().findParsByTlgId(tlg.getTlgId());
        paragraphs = "";
        if (!parslist.isEmpty()) {
            for (ParagraphEntity paragraph : parslist) {
                paragraphs = paragraphs + paragraph.getSecrecyParagraphId() + ",";
            }
            if (parslist.size() == 1) {
                /** вынести в настройки **/
                paragraphs = "п. " + paragraphs.substring(0,paragraphs.length() - 1) + " Перечня";
            } else paragraphs = "пп. " + paragraphs.substring(0,paragraphs.length() - 1) + " Перечня";
        }

        mailings = MailingDAO.getInstance().findByTlgId(tlg.getTlgId());
        typeInTlgs = TypeInTlgDAO.getInstance().findByTlgId(tlg.getTlgId());

    }

    public void createDOC() {
        initalizeTagValue();
        try {
            // 1) Load Docx file by filling Velocity template engine and cache it to the registry
            File template = new File("template\\template01.docx");
            InputStream in = FileUtils.openInputStream(template);
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in,TemplateEngineKind.Velocity);
            FieldsMetadata metadata = report.createFieldsMetadata();
            metadata.addFieldAsImage( "qr" );

            // 2) Заполняем модель для docx
            //QR и адреса
            List<TmpAddress> addresses = new ArrayList<>();
            String addressCallsign, addressName, addressPerson;
            String formatedAddress = "";
            for (Mailing mailing : mailings) {
                addressCallsign =  mailing.getAddress().getTlgAddressCallsign();
                addressName =  mailing.getAddress().getTlgAddressName();
                addressPerson = mailing.getAddress().getTlgAddressPerson();
                TmpAddress tmpAddress = new TmpAddress(
                    mailing.getAddress().getTlgAddressCallsign(),
                    mailing.getAddress().getTlgAddressName(),
                    mailing.getAddress().getTlgAddressPerson(),
                    mailing.getAddress().getTlgAddressPersonRespect()
                );
                addresses.add(tmpAddress);
                if(mailing.getAddress().getTlgAddressCallsign() != null) {
                    formatedAddress = formatedAddress + addressCallsign + " ";
                }
                if (mailing.getAddress().getTlgAddressName() != null) {
                    formatedAddress = formatedAddress + addressName + " ";
                } else formatedAddress = formatedAddress + addressName + "/";
                if (mailing.getAddress().getTlgAddressPerson() != null) {
                    formatedAddress = formatedAddress + addressPerson + "/";
                }
            }
            String contents = tlg.getTitle() + "/" + formatedAddress + "/" +
                    tlg.getSupervisor().getPosition() + " " + tlg.getSupervisor().getLastname() + "/" +
                    tlg.getTlgDateGreate();
            QrCodeCreateUtil qrCodeCreateUtil = new QrCodeCreateUtil();
            File tmpQrFile = new File("template\\qr.jpeg");
            qrCodeCreateUtil.createQrCode(new FileOutputStream(tmpQrFile),
                    contents,
                    170,
                    "JPEG");
            qrCodeCreateUtil.readQrCode(new FileInputStream(tmpQrFile));
            IImageProvider qr = new FileImageProvider(new File( "template\\qr.jpeg"), true);
            IContext context = report.createContext();

            //оригинальные поля
            UserLoginController.getInstance();
            User user = UserLoginController.getCurrentUser();
            String s = tlg.getText();
            Pattern pattern = Pattern.compile("<p class=\\\"tlg-text\\\">.*?</p>");
            Matcher m = pattern.matcher(s);
            ArrayList<String> pars = new ArrayList<>();
            while (m.find()) {
                pars.add(m.group().subSequence(20, m.group().length()-4).toString());
            }

            TagDTO tmp = new TagDTO(
                tlg.getTitle(),
                tlg.getSecrecy().getSecrecyName(),
                paragraphs,
                tlg.getCategory().getCategoryName(),
                pars,
                tlg.getSupervisor().getPosition(),
                tlg.getSupervisor().getRank().getNameRank(),
                tlg.getSupervisor().getLastname(),
                user.getUserPosition(),
                user.getUserFIO(),
                user.getUserTelephone(),
                propertyApp.get().getNumberComputer()
            );

            //типы
            List<Type> typeslist = TypeInTlgDAO.getInstance().findByTlgId(tlg.getTlgId());
            List<String> types = new ArrayList<String>();
            if (!typeslist.isEmpty()) {
                for (Type typeInTlg : typeslist) {
                    types.add(typeInTlg.getTypeName());
                }
            }

            //приложения к телеграмме
            ApplicationDocDAO applicationDocDAO = ApplicationDocDAO.getInstance();
            ParsInAppsDAO parsInAppsDAO = ParsInAppsDAO.getInstance();
            List<ApplicationDoc> apps  = applicationDocDAO.findAllFromTlg(tlg.getTlgId());
            ArrayList<String> lapps = new ArrayList<>();
            String appstr = "false";
            String endapp = "";
            if(!apps.isEmpty()) {
                endapp = pickPhrase(apps.size(), "ий", "ие", "ия");
                for(ApplicationDoc app : apps) {
                    appstr = app.getAppName() + "." + app.getAppExt() + ", " + app.getAppExe() +
                            ", " + app.getAppSize() + ", " + app.getAppSec().toString().toLowerCase(Locale.ROOT);
                    List<ParagraphEntity> ppList = parsInAppsDAO.findParsByAppId(app.getAppId());
                    if(!ppList.isEmpty()) {
                        appstr = appstr + " (п. ";
                        for (ParagraphEntity p : ppList) {
                            appstr = appstr + p.getSecrecyParagraphId() + ", ";
                        }
                        appstr = appstr.substring(0, appstr.length() - 2);
                        appstr = appstr + " Перечня)";
                    }
                    appstr = appstr + ", " + app.getAppNumb();
                    lapps.add(appstr);
                }
            }

            //заполняем шаблон
            context.put("tmp", tmp);
            context.put("types", types);
            context.put( "qr", qr);
            context.put( "addresses", addresses);
            context.put( "lapps", lapps);
            context.put( "endapp", endapp);
            context.put( "capp", apps.size());

            // 3) Generate report by merging Java model with the Docx
            String nameFile = tlg.getTitle();
            nameFile = nameFile.replaceAll("\\/","_");
            File fileDOCX = new File(propertyApp.get().getPathDOC() + "\\" + nameFile + ".docx");
            OutputStream out = new FileOutputStream(fileDOCX);
            report.process(context, out);
            tmpQrFile.delete();
        } catch (IOException | WriterException e) {
            logger.error("Error", e);
        } catch (XDocReportException e) {
            logger.error("Error", e);
        }
    }

    //замена окончаний слов в зависимости от количества
    public static String pickPhrase(int count, String word0,String word1,String word2) {
        int rem = count % 100;
        if(rem < 11 || rem > 14){
            rem = count % 10;
            if(rem == 1) return word1;
            if(rem >= 2 && rem <= 4) return word2;
        } return word0;
    }
}
