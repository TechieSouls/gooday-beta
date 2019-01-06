package com.deploy.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mandeep on 24/12/18.
 */

public class AuthenticateService {

    public String getPhoneCodeByCountryCode(String countryCode) {
        Map<String, String> countryPhoneCodeMap = new HashMap<>();
        countryPhoneCodeMap.put("AF","93");countryPhoneCodeMap.put("AL","355");countryPhoneCodeMap.put("DZ","213");countryPhoneCodeMap.put("AD","376");countryPhoneCodeMap.put("AO","244");countryPhoneCodeMap.put("AQ","672");countryPhoneCodeMap.put("AR","54");countryPhoneCodeMap.put("AM","374");countryPhoneCodeMap.put("AW","297");countryPhoneCodeMap.put("AU","61");countryPhoneCodeMap.put("AT","43");countryPhoneCodeMap.put("AZ","994");countryPhoneCodeMap.put("BH","973");countryPhoneCodeMap.put("BD","880");countryPhoneCodeMap.put("BY","375");countryPhoneCodeMap.put("BE","32");countryPhoneCodeMap.put("BZ","501");countryPhoneCodeMap.put("BJ","229");countryPhoneCodeMap.put("BT","975");countryPhoneCodeMap.put("BO","591");countryPhoneCodeMap.put("BA","387");countryPhoneCodeMap.put("BW","267");countryPhoneCodeMap.put("BR","55");countryPhoneCodeMap.put("BN","673");countryPhoneCodeMap.put("BG","359");countryPhoneCodeMap.put("BF","226");countryPhoneCodeMap.put("MM","95");countryPhoneCodeMap.put("BI","257");countryPhoneCodeMap.put("KH","855");countryPhoneCodeMap.put("CM","237");countryPhoneCodeMap.put("CA","1");countryPhoneCodeMap.put("CV","238");countryPhoneCodeMap.put("CF","236");countryPhoneCodeMap.put("TD","235");countryPhoneCodeMap.put("CL","56");countryPhoneCodeMap.put("CN","86");countryPhoneCodeMap.put("CX","61");countryPhoneCodeMap.put("CC","61");countryPhoneCodeMap.put("CO","57");countryPhoneCodeMap.put("KM","269");countryPhoneCodeMap.put("CG","242");countryPhoneCodeMap.put("CD","243");countryPhoneCodeMap.put("CK","682");countryPhoneCodeMap.put("CR","506");countryPhoneCodeMap.put("HR","385");countryPhoneCodeMap.put("CU","53");countryPhoneCodeMap.put("CY","357");countryPhoneCodeMap.put("CZ","420");countryPhoneCodeMap.put("DK","45");countryPhoneCodeMap.put("DJ","253");countryPhoneCodeMap.put("TL","670");countryPhoneCodeMap.put("EC","593");countryPhoneCodeMap.put("EG","20");countryPhoneCodeMap.put("SV","503");countryPhoneCodeMap.put("GQ","240");countryPhoneCodeMap.put("ER","291");countryPhoneCodeMap.put("EE","372");countryPhoneCodeMap.put("ET","251");countryPhoneCodeMap.put("FK","500");countryPhoneCodeMap.put("FO","298");countryPhoneCodeMap.put("FJ","679");countryPhoneCodeMap.put("FI","358");countryPhoneCodeMap.put("FR","33");countryPhoneCodeMap.put("PF","689");countryPhoneCodeMap.put("GA","241");countryPhoneCodeMap.put("GM","220");countryPhoneCodeMap.put("GE","995");countryPhoneCodeMap.put("DE","49");countryPhoneCodeMap.put("GH","233");countryPhoneCodeMap.put("GI","350");countryPhoneCodeMap.put("GR","30");countryPhoneCodeMap.put("GL","299");countryPhoneCodeMap.put("GT","502");countryPhoneCodeMap.put("GN","224");countryPhoneCodeMap.put("GW","245");countryPhoneCodeMap.put("GY","592");countryPhoneCodeMap.put("HT","509");countryPhoneCodeMap.put("HN","504");countryPhoneCodeMap.put("HK","852");countryPhoneCodeMap.put("HU","36");countryPhoneCodeMap.put("IN","91");countryPhoneCodeMap.put("ID","62");countryPhoneCodeMap.put("IR","98");countryPhoneCodeMap.put("IQ","964");countryPhoneCodeMap.put("IE","353");countryPhoneCodeMap.put("IM","44");countryPhoneCodeMap.put("IL","972");countryPhoneCodeMap.put("IT","39");countryPhoneCodeMap.put("CI","225");countryPhoneCodeMap.put("JP","81");countryPhoneCodeMap.put("JO","962");countryPhoneCodeMap.put("KZ","7");countryPhoneCodeMap.put("KE","254");countryPhoneCodeMap.put("KI","686");countryPhoneCodeMap.put("KW","965");countryPhoneCodeMap.put("KG","996");countryPhoneCodeMap.put("LA","856");countryPhoneCodeMap.put("LV","371");countryPhoneCodeMap.put("LB","961");countryPhoneCodeMap.put("LS","266");countryPhoneCodeMap.put("LR","231");countryPhoneCodeMap.put("LY","218");countryPhoneCodeMap.put("LI","423");countryPhoneCodeMap.put("LT","370");countryPhoneCodeMap.put("LU","352");countryPhoneCodeMap.put("MO","853");countryPhoneCodeMap.put("MK","389");countryPhoneCodeMap.put("MG","261");countryPhoneCodeMap.put("MW","265");countryPhoneCodeMap.put("MY","60");countryPhoneCodeMap.put("MV","960");countryPhoneCodeMap.put("ML","223");countryPhoneCodeMap.put("MT","356");countryPhoneCodeMap.put("MH","692");countryPhoneCodeMap.put("MR","222");countryPhoneCodeMap.put("MU","230");countryPhoneCodeMap.put("YT","262");countryPhoneCodeMap.put("MX","52");countryPhoneCodeMap.put("FM","691");countryPhoneCodeMap.put("MD","373");countryPhoneCodeMap.put("MC","377");countryPhoneCodeMap.put("MN","976");countryPhoneCodeMap.put("ME","382");countryPhoneCodeMap.put("MA","212");countryPhoneCodeMap.put("MZ","258");countryPhoneCodeMap.put("NA","264");countryPhoneCodeMap.put("NR","674");countryPhoneCodeMap.put("NP","977");countryPhoneCodeMap.put("NL","31");countryPhoneCodeMap.put("AN","599");countryPhoneCodeMap.put("NC","687");countryPhoneCodeMap.put("NZ","64");countryPhoneCodeMap.put("NI","505");countryPhoneCodeMap.put("NE","227");countryPhoneCodeMap.put("NG","234");countryPhoneCodeMap.put("NU","683");countryPhoneCodeMap.put("KP","850");countryPhoneCodeMap.put("NO","47");countryPhoneCodeMap.put("OM","968");countryPhoneCodeMap.put("PK","92");countryPhoneCodeMap.put("PW","680");countryPhoneCodeMap.put("PA","507");countryPhoneCodeMap.put("PG","675");countryPhoneCodeMap.put("PY","595");countryPhoneCodeMap.put("PE","51");countryPhoneCodeMap.put("PH","63");countryPhoneCodeMap.put("PN","870");countryPhoneCodeMap.put("PL","48");countryPhoneCodeMap.put("PT","351");countryPhoneCodeMap.put("PR","1");countryPhoneCodeMap.put("QA","974");countryPhoneCodeMap.put("RO","40");countryPhoneCodeMap.put("RU","7");countryPhoneCodeMap.put("RW","250");countryPhoneCodeMap.put("BL","590");countryPhoneCodeMap.put("WS","685");countryPhoneCodeMap.put("SM","378");countryPhoneCodeMap.put("ST","239");countryPhoneCodeMap.put("SA","966");countryPhoneCodeMap.put("SN","221");countryPhoneCodeMap.put("RS","381");countryPhoneCodeMap.put("SC","248");countryPhoneCodeMap.put("SL","232");countryPhoneCodeMap.put("SG","65");countryPhoneCodeMap.put("SK","421");countryPhoneCodeMap.put("SI","386");countryPhoneCodeMap.put("SB","677");countryPhoneCodeMap.put("SO","252");countryPhoneCodeMap.put("ZA","27");countryPhoneCodeMap.put("KR","82");countryPhoneCodeMap.put("ES","34");countryPhoneCodeMap.put("LK","94");countryPhoneCodeMap.put("SH","290");countryPhoneCodeMap.put("PM","508");countryPhoneCodeMap.put("SD","249");countryPhoneCodeMap.put("SR","597");countryPhoneCodeMap.put("SZ","268");countryPhoneCodeMap.put("SE","46");countryPhoneCodeMap.put("CH","41");countryPhoneCodeMap.put("SY","963");countryPhoneCodeMap.put("TW","886");countryPhoneCodeMap.put("TJ","992");countryPhoneCodeMap.put("TZ","255");countryPhoneCodeMap.put("TH","66");countryPhoneCodeMap.put("TG","228");countryPhoneCodeMap.put("TK","690");countryPhoneCodeMap.put("TO","676");countryPhoneCodeMap.put("TN","216");countryPhoneCodeMap.put("TR","90");countryPhoneCodeMap.put("TM","993");countryPhoneCodeMap.put("TV","688");countryPhoneCodeMap.put("AE","971");countryPhoneCodeMap.put("UG","256");countryPhoneCodeMap.put("GB","44");countryPhoneCodeMap.put("UA","380");countryPhoneCodeMap.put("UY","598");countryPhoneCodeMap.put("US","1");countryPhoneCodeMap.put("UZ","998");countryPhoneCodeMap.put("VU","678");countryPhoneCodeMap.put("VA","39");countryPhoneCodeMap.put("VE","58");countryPhoneCodeMap.put("VN","84");countryPhoneCodeMap.put("WF","681");countryPhoneCodeMap.put("YE","967");countryPhoneCodeMap.put("ZM","260");countryPhoneCodeMap.put("ZW","263");
        if (countryPhoneCodeMap.containsKey(countryCode)) {
            return countryPhoneCodeMap.get(countryCode);
        }
        return "00";
    }
}
