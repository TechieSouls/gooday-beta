package com.cenes.bo;

import android.support.annotation.NonNull;

import com.cenes.R;

import java.text.Collator;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mandeep on 21/9/18.
 */

public class CountryCode implements Comparable<CountryCode>{

    static int DEFAULT_FLAG_RES = -99;

    private String phoneCode;
    private String nameCode;
    private String name;
    private Integer flagResID = DEFAULT_FLAG_RES;

    public CountryCode() {

    }
    public CountryCode(String nameCode, String phoneCode, String name, int flagResID) {
        this.nameCode = nameCode;
        this.phoneCode = phoneCode;
        this.name = name;
        this.flagResID = flagResID;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getNameCode() {
        return nameCode;
    }

    public void setNameCode(String nameCode) {
        this.nameCode = nameCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getFlagResID() {
        return flagResID;
    }

    public void setFlagResID(Integer flagResID) {
        this.flagResID = flagResID;
    }

    /**
     * Returns image res based on country name code
     *
     * @param
     * @return
     */
    public static int getFlagMasterResID(CountryCode countryCode) {
        switch (countryCode.getNameCode().toLowerCase()) {
            //this should be sorted based on country name code.
            case "ad": //andorra
                return R.drawable.flag_andorra;
            case "ae": //united arab emirates
                return R.drawable.flag_uae;
            case "af": //afghanistan
                return R.drawable.flag_afghanistan;
            case "ag": //antigua & barbuda
                return R.drawable.flag_antigua_and_barbuda;
            case "ai": //anguilla // Caribbean Islands
                return R.drawable.flag_anguilla;
            case "al": //albania
                return R.drawable.flag_albania;
            case "am": //armenia
                return R.drawable.flag_armenia;
            case "ao": //angola
                return R.drawable.flag_angola;
            case "aq": //antarctica // custom
                return R.drawable.flag_antarctica;
            case "ar": //argentina
                return R.drawable.flag_argentina;
            case "as": //American Samoa
                return R.drawable.flag_american_samoa;
            case "at": //austria
                return R.drawable.flag_austria;
            case "au": //australia
                return R.drawable.flag_australia;
            case "aw": //aruba
                return R.drawable.flag_aruba;
            case "ax": //alan islands
                return R.drawable.flag_aland;
            case "az": //azerbaijan
                return R.drawable.flag_azerbaijan;
            case "ba": //bosnia and herzegovina
                return R.drawable.flag_bosnia;
            case "bb": //barbados
                return R.drawable.flag_barbados;
            case "bd": //bangladesh
                return R.drawable.flag_bangladesh;
            case "be": //belgium
                return R.drawable.flag_belgium;
            case "bf": //burkina faso
                return R.drawable.flag_burkina_faso;
            case "bg": //bulgaria
                return R.drawable.flag_bulgaria;
            case "bh": //bahrain
                return R.drawable.flag_bahrain;
            case "bi": //burundi
                return R.drawable.flag_burundi;
            case "bj": //benin
                return R.drawable.flag_benin;
            case "bl": //saint barthélemy
                return R.drawable.flag_saint_barthelemy;// custom
            case "bm": //bermuda
                return R.drawable.flag_bermuda;
            case "bn": //brunei darussalam // custom
                return R.drawable.flag_brunei;
            case "bo": //bolivia, plurinational state of
                return R.drawable.flag_bolivia;
            case "br": //brazil
                return R.drawable.flag_brazil;
            case "bs": //bahamas
                return R.drawable.flag_bahamas;
            case "bt": //bhutan
                return R.drawable.flag_bhutan;
            case "bw": //botswana
                return R.drawable.flag_botswana;
            case "by": //belarus
                return R.drawable.flag_belarus;
            case "bz": //belize
                return R.drawable.flag_belize;
            case "ca": //canada
                return R.drawable.flag_canada;
            case "cc": //cocos (keeling) islands
                return R.drawable.flag_cocos;// custom
            case "cd": //congo, the democratic republic of the
                return R.drawable.flag_democratic_republic_of_the_congo;
            case "cf": //central african republic
                return R.drawable.flag_central_african_republic;
            case "cg": //congo
                return R.drawable.flag_republic_of_the_congo;
            case "ch": //switzerland
                return R.drawable.flag_switzerland;
            case "ci": //côte d\'ivoire
                return R.drawable.flag_cote_divoire;
            case "ck": //cook islands
                return R.drawable.flag_cook_islands;
            case "cl": //chile
                return R.drawable.flag_chile;
            case "cm": //cameroon
                return R.drawable.flag_cameroon;
            case "cn": //china
                return R.drawable.flag_china;
            case "co": //colombia
                return R.drawable.flag_colombia;
            case "cr": //costa rica
                return R.drawable.flag_costa_rica;
            case "cu": //cuba
                return R.drawable.flag_cuba;
            case "cv": //cape verde
                return R.drawable.flag_cape_verde;
            case "cx": //christmas island
                return R.drawable.flag_christmas_island;
            case "cy": //cyprus
                return R.drawable.flag_cyprus;
            case "cz": //czech republic
                return R.drawable.flag_czech_republic;
            case "de": //germany
                return R.drawable.flag_germany;
            case "dj": //djibouti
                return R.drawable.flag_djibouti;
            case "dk": //denmark
                return R.drawable.flag_denmark;
            case "dm": //dominica
                return R.drawable.flag_dominica;
            case "do": //dominican republic
                return R.drawable.flag_dominican_republic;
            case "dz": //algeria
                return R.drawable.flag_algeria;
            case "ec": //ecuador
                return R.drawable.flag_ecuador;
            case "ee": //estonia
                return R.drawable.flag_estonia;
            case "eg": //egypt
                return R.drawable.flag_egypt;
            case "er": //eritrea
                return R.drawable.flag_eritrea;
            case "es": //spain
                return R.drawable.flag_spain;
            case "et": //ethiopia
                return R.drawable.flag_ethiopia;
            case "fi": //finland
                return R.drawable.flag_finland;
            case "fj": //fiji
                return R.drawable.flag_fiji;
            case "fk": //falkland islands (malvinas)
                return R.drawable.flag_falkland_islands;
            case "fm": //micronesia, federated states of
                return R.drawable.flag_micronesia;
            case "fo": //faroe islands
                return R.drawable.flag_faroe_islands;
            case "fr": //france
                return R.drawable.flag_france;
            case "ga": //gabon
                return R.drawable.flag_gabon;
            case "gb": //united kingdom
                return R.drawable.flag_united_kingdom;
            case "gd": //grenada
                return R.drawable.flag_grenada;
            case "ge": //georgia
                return R.drawable.flag_georgia;
            case "gf": //guyane
                return R.drawable.flag_guyane;
            case "gg": //Guernsey
                return R.drawable.flag_guernsey;
            case "gh": //ghana
                return R.drawable.flag_ghana;
            case "gi": //gibraltar
                return R.drawable.flag_gibraltar;
            case "gl": //greenland
                return R.drawable.flag_greenland;
            case "gm": //gambia
                return R.drawable.flag_gambia;
            case "gn": //guinea
                return R.drawable.flag_guinea;
            case "gp": //guadeloupe
                return R.drawable.flag_guadeloupe;
            case "gq": //equatorial guinea
                return R.drawable.flag_equatorial_guinea;
            case "gr": //greece
                return R.drawable.flag_greece;
            case "gt": //guatemala
                return R.drawable.flag_guatemala;
            case "gu": //Guam
                return R.drawable.flag_guam;
            case "gw": //guinea-bissau
                return R.drawable.flag_guinea_bissau;
            case "gy": //guyana
                return R.drawable.flag_guyana;
            case "hk": //hong kong
                return R.drawable.flag_hong_kong;
            case "hn": //honduras
                return R.drawable.flag_honduras;
            case "hr": //croatia
                return R.drawable.flag_croatia;
            case "ht": //haiti
                return R.drawable.flag_haiti;
            case "hu": //hungary
                return R.drawable.flag_hungary;
            case "id": //indonesia
                return R.drawable.flag_indonesia;
            case "ie": //ireland
                return R.drawable.flag_ireland;
            case "il": //israel
                return R.drawable.flag_israel;
            case "im": //isle of man
                return R.drawable.flag_isleof_man; // custom
            case "is": //Iceland
                return R.drawable.flag_iceland;
            case "in": //india
                return R.drawable.flag_india;
            case "io": //British indian ocean territory
                return R.drawable.flag_british_indian_ocean_territory;
            case "iq": //iraq
                return R.drawable.flag_iraq_new;
            case "ir": //iran, islamic republic of
                return R.drawable.flag_iran;
            case "it": //italy
                return R.drawable.flag_italy;
            case "je": //Jersey
                return R.drawable.flag_jersey;
            case "jm": //jamaica
                return R.drawable.flag_jamaica;
            case "jo": //jordan
                return R.drawable.flag_jordan;
            case "jp": //japan
                return R.drawable.flag_japan;
            case "ke": //kenya
                return R.drawable.flag_kenya;
            case "kg": //kyrgyzstan
                return R.drawable.flag_kyrgyzstan;
            case "kh": //cambodia
                return R.drawable.flag_cambodia;
            case "ki": //kiribati
                return R.drawable.flag_kiribati;
            case "km": //comoros
                return R.drawable.flag_comoros;
            case "kn": //st kitts & nevis
                return R.drawable.flag_saint_kitts_and_nevis;
            case "kp": //north korea
                return R.drawable.flag_north_korea;
            case "kr": //south korea
                return R.drawable.flag_south_korea;
            case "kw": //kuwait
                return R.drawable.flag_kuwait;
            case "ky": //Cayman_Islands
                return R.drawable.flag_cayman_islands;
            case "kz": //kazakhstan
                return R.drawable.flag_kazakhstan;
            case "la": //lao people\'s democratic republic
                return R.drawable.flag_laos;
            case "lb": //lebanon
                return R.drawable.flag_lebanon;
            case "lc": //st lucia
                return R.drawable.flag_saint_lucia;
            case "li": //liechtenstein
                return R.drawable.flag_liechtenstein;
            case "lk": //sri lanka
                return R.drawable.flag_sri_lanka;
            case "lr": //liberia
                return R.drawable.flag_liberia;
            case "ls": //lesotho
                return R.drawable.flag_lesotho;
            case "lt": //lithuania
                return R.drawable.flag_lithuania;
            case "lu": //luxembourg
                return R.drawable.flag_luxembourg;
            case "lv": //latvia
                return R.drawable.flag_latvia;
            case "ly": //libya
                return R.drawable.flag_libya;
            case "ma": //morocco
                return R.drawable.flag_morocco;
            case "mc": //monaco
                return R.drawable.flag_monaco;
            case "md": //moldova, republic of
                return R.drawable.flag_moldova;
            case "me": //montenegro
                return R.drawable.flag_of_montenegro;// custom
            case "mf":
                return R.drawable.flag_saint_martin;
            case "mg": //madagascar
                return R.drawable.flag_madagascar;
            case "mh": //marshall islands
                return R.drawable.flag_marshall_islands;
            case "mk": //macedonia, the former yugoslav republic of
                return R.drawable.flag_macedonia;
            case "ml": //mali
                return R.drawable.flag_mali;
            case "mm": //myanmar
                return R.drawable.flag_myanmar;
            case "mn": //mongolia
                return R.drawable.flag_mongolia;
            case "mo": //macao
                return R.drawable.flag_macao;
            case "mp": // Northern mariana islands
                return R.drawable.flag_northern_mariana_islands;
            case "mq": //martinique
                return R.drawable.flag_martinique;
            case "mr": //mauritania
                return R.drawable.flag_mauritania;
            case "ms": //montserrat
                return R.drawable.flag_montserrat;
            case "mt": //malta
                return R.drawable.flag_malta;
            case "mu": //mauritius
                return R.drawable.flag_mauritius;
            case "mv": //maldives
                return R.drawable.flag_maldives;
            case "mw": //malawi
                return R.drawable.flag_malawi;
            case "mx": //mexico
                return R.drawable.flag_mexico;
            case "my": //malaysia
                return R.drawable.flag_malaysia;
            case "mz": //mozambique
                return R.drawable.flag_mozambique;
            case "na": //namibia
                return R.drawable.flag_namibia;
            case "nc": //new caledonia
                return R.drawable.flag_new_caledonia;// custom
            case "ne": //niger
                return R.drawable.flag_niger;
            case "nf": //Norfolk
                return R.drawable.flag_norfolk_island;
            case "ng": //nigeria
                return R.drawable.flag_nigeria;
            case "ni": //nicaragua
                return R.drawable.flag_nicaragua;
            case "nl": //netherlands
                return R.drawable.flag_netherlands;
            case "no": //norway
                return R.drawable.flag_norway;
            case "np": //nepal
                return R.drawable.flag_nepal;
            case "nr": //nauru
                return R.drawable.flag_nauru;
            case "nu": //niue
                return R.drawable.flag_niue;
            case "nz": //new zealand
                return R.drawable.flag_new_zealand;
            case "om": //oman
                return R.drawable.flag_oman;
            case "pa": //panama
                return R.drawable.flag_panama;
            case "pe": //peru
                return R.drawable.flag_peru;
            case "pf": //french polynesia
                return R.drawable.flag_french_polynesia;
            case "pg": //papua new guinea
                return R.drawable.flag_papua_new_guinea;
            case "ph": //philippines
                return R.drawable.flag_philippines;
            case "pk": //pakistan
                return R.drawable.flag_pakistan;
            case "pl": //poland
                return R.drawable.flag_poland;
            case "pm": //saint pierre and miquelon
                return R.drawable.flag_saint_pierre;
            case "pn": //pitcairn
                return R.drawable.flag_pitcairn_islands;
            case "pr": //puerto rico
                return R.drawable.flag_puerto_rico;
            case "ps": //palestine
                return R.drawable.flag_palestine;
            case "pt": //portugal
                return R.drawable.flag_portugal;
            case "pw": //palau
                return R.drawable.flag_palau;
            case "py": //paraguay
                return R.drawable.flag_paraguay;
            case "qa": //qatar
                return R.drawable.flag_qatar;
            case "re": //la reunion
                return R.drawable.flag_martinique; // no exact flag found
            case "ro": //romania
                return R.drawable.flag_romania;
            case "rs": //serbia
                return R.drawable.flag_serbia; // custom
            case "ru": //russian federation
                return R.drawable.flag_russian_federation;
            case "rw": //rwanda
                return R.drawable.flag_rwanda;
            case "sa": //saudi arabia
                return R.drawable.flag_saudi_arabia;
            case "sb": //solomon islands
                return R.drawable.flag_soloman_islands;
            case "sc": //seychelles
                return R.drawable.flag_seychelles;
            case "sd": //sudan
                return R.drawable.flag_sudan;
            case "se": //sweden
                return R.drawable.flag_sweden;
            case "sg": //singapore
                return R.drawable.flag_singapore;
            case "sh": //saint helena, ascension and tristan da cunha
                return R.drawable.flag_saint_helena; // custom
            case "si": //slovenia
                return R.drawable.flag_slovenia;
            case "sk": //slovakia
                return R.drawable.flag_slovakia;
            case "sl": //sierra leone
                return R.drawable.flag_sierra_leone;
            case "sm": //san marino
                return R.drawable.flag_san_marino;
            case "sn": //senegal
                return R.drawable.flag_senegal;
            case "so": //somalia
                return R.drawable.flag_somalia;
            case "sr": //suriname
                return R.drawable.flag_suriname;
            case "st": //sao tome and principe
                return R.drawable.flag_sao_tome_and_principe;
            case "sv": //el salvador
                return R.drawable.flag_el_salvador;
            case "sx": //sint maarten
                return R.drawable.flag_sint_maarten;
            case "sy": //syrian arab republic
                return R.drawable.flag_syria;
            case "sz": //swaziland
                return R.drawable.flag_swaziland;
            case "tc": //turks & caicos islands
                return R.drawable.flag_turks_and_caicos_islands;
            case "td": //chad
                return R.drawable.flag_chad;
            case "tg": //togo
                return R.drawable.flag_togo;
            case "th": //thailand
                return R.drawable.flag_thailand;
            case "tj": //tajikistan
                return R.drawable.flag_tajikistan;
            case "tk": //tokelau
                return R.drawable.flag_tokelau; // custom
            case "tl": //timor-leste
                return R.drawable.flag_timor_leste;
            case "tm": //turkmenistan
                return R.drawable.flag_turkmenistan;
            case "tn": //tunisia
                return R.drawable.flag_tunisia;
            case "to": //tonga
                return R.drawable.flag_tonga;
            case "tr": //turkey
                return R.drawable.flag_turkey;
            case "tt": //trinidad & tobago
                return R.drawable.flag_trinidad_and_tobago;
            case "tv": //tuvalu
                return R.drawable.flag_tuvalu;
            case "tw": //taiwan, province of china
                return R.drawable.flag_taiwan;
            case "tz": //tanzania, united republic of
                return R.drawable.flag_tanzania;
            case "ua": //ukraine
                return R.drawable.flag_ukraine;
            case "ug": //uganda
                return R.drawable.flag_uganda;
            case "us": //united states
                return R.drawable.flag_united_states_of_america;
            case "uy": //uruguay
                return R.drawable.flag_uruguay;
            case "uz": //uzbekistan
                return R.drawable.flag_uzbekistan;
            case "va": //holy see (vatican city state)
                return R.drawable.flag_vatican_city;
            case "vc": //st vincent & the grenadines
                return R.drawable.flag_saint_vicent_and_the_grenadines;
            case "ve": //venezuela, bolivarian republic of
                return R.drawable.flag_venezuela;
            case "vg": //british virgin islands
                return R.drawable.flag_british_virgin_islands;
            case "vi": //us virgin islands
                return R.drawable.flag_us_virgin_islands;
            case "vn": //vietnam
                return R.drawable.flag_vietnam;
            case "vu": //vanuatu
                return R.drawable.flag_vanuatu;
            case "wf": //wallis and futuna
                return R.drawable.flag_wallis_and_futuna;
            case "ws": //samoa
                return R.drawable.flag_samoa;
            case "xk": //kosovo
                return R.drawable.flag_kosovo;
            case "ye": //yemen
                return R.drawable.flag_yemen;
            case "yt": //mayotte
                return R.drawable.flag_martinique; // no exact flag found
            case "za": //south africa
                return R.drawable.flag_south_africa;
            case "zm": //zambia
                return R.drawable.flag_zambia;
            case "zw": //zimbabwe
                return R.drawable.flag_zimbabwe;
            default:
                return R.drawable.flag_transparent;
        }
    }

    public static List<CountryCode> getLibraryMasterCountriesEnglish() {
        List<CountryCode> countries = new ArrayList<>();
        countries.add(new CountryCode("ad", "+376", "Andorra", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ae", "+971", "United Arab Emirates (UAE)", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("af", "+93", "Afghanistan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ag", "+1", "Antigua and Barbuda", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ai", "+1", "Anguilla", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("al", "+355", "Albania", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("am", "+374", "Armenia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ao", "+244", "Angola", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("aq", "+672", "Antarctica", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ar", "+54", "Argentina", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("as", "+1", "American Samoa", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("at", "+43", "Austria", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("au", "+61", "Australia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("aw", "+297", "Aruba", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("az", "+358", "Aland Islands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("az", "+994", "Azerbaijan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ba", "+387", "Bosnia And Herzegovina", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bb", "+1", "Barbados", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bd", "+880", "Bangladesh", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("be", "+32", "Belgium", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bf", "+226", "Burkina Faso", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bg", "+359", "Bulgaria", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bh", "+973", "Bahrain", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bi", "+257", "Burundi", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bj", "+229", "Benin", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bl", "+590", "Saint Barthélemy", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bm", "+1", "Bermuda", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bn", "+673", "Brunei Darussalam", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bo", "+591", "Bolivia, Plurinational State Of", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("br", "+55", "Brazil", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bs", "+1", "Bahamas", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bt", "+975", "Bhutan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bw", "+267", "Botswana", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("by", "+375", "Belarus", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("bz", "+501", "Belize", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ca", "+1", "Canada", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cc", "+61", "Cocos (keeling) Islands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cd", "+243", "Congo, The Democratic Republic Of The", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cf", "+236", "Central African Republic", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cg", "+242", "Congo", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ch", "+41", "Switzerland", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ci", "+225", "Côte D'ivoire", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ck", "+682", "Cook Islands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cl", "+56", "Chile", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cm", "+237", "Cameroon", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cn", "+86", "China", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("co", "+57", "Colombia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cr", "+506", "Costa Rica", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cu", "+53", "Cuba", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cv", "+238", "Cape Verde", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cx", "+61", "Christmas Island", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cy", "+357", "Cyprus", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("cz", "+420", "Czech Republic", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("de", "+49", "Germany", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("dj", "+253", "Djibouti", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("dk", "+45", "Denmark", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("dm", "+1", "Dominica", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("do", "+1", "Dominican Republic", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("dz", "+213", "Algeria", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ec", "+593", "Ecuador", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ee", "+372", "Estonia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("eg", "+20", "Egypt", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("er", "+291", "Eritrea", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("es", "+34", "Spain", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("et", "+251", "Ethiopia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("fi", "+358", "Finland", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("fj", "+679", "Fiji", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("fk", "+500", "Falkland Islands (malvinas)", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("fm", "+691", "Micronesia, Federated States Of", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("fo", "+298", "Faroe Islands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("fr", "+33", "France", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ga", "+241", "Gabon", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gb", "+44", "United Kingdom", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gd", "+1", "Grenada", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ge", "+995", "Georgia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gf", "+594", "French Guyana", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gh", "+233", "Ghana", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gi", "+350", "Gibraltar", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gl", "+299", "Greenland", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gm", "+220", "Gambia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gn", "+224", "Guinea", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gp", "+450", "Guadeloupe", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gq", "+240", "Equatorial Guinea", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gr", "+30", "Greece", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gt", "+502", "Guatemala", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gu", "+1", "Guam", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gw", "+245", "Guinea-bissau", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("gy", "+592", "Guyana", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("hk", "+852", "Hong Kong", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("hn", "+504", "Honduras", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("hr", "+385", "Croatia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ht", "+509", "Haiti", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("hu", "+36", "Hungary", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("id", "+62", "Indonesia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ie", "+353", "Ireland", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("il", "+972", "Israel", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("im", "+44", "Isle Of Man", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("is", "+354", "Iceland", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("in", "+91", "India", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("io", "+246", "British Indian Ocean Territory", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("iq", "+964", "Iraq", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ir", "+98", "Iran, Islamic Republic Of", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("it", "+39", "Italy", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("je", "+44", "Jersey ", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("jm", "+1", "Jamaica", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("jo", "+962", "Jordan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("jp", "+81", "Japan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ke", "+254", "Kenya", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("kg", "+996", "Kyrgyzstan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("kh", "+855", "Cambodia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ki", "+686", "Kiribati", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("km", "+269", "Comoros", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("kn", "+1", "Saint Kitts and Nevis", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("kp", "+850", "North Korea", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("kr", "+82", "South Korea", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("kw", "+965", "Kuwait", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ky", "+1", "Cayman Islands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("kz", "+7", "Kazakhstan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("la", "+856", "Lao People's Democratic Republic", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("lb", "+961", "Lebanon", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("lc", "+1", "Saint Lucia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("li", "+423", "Liechtenstein", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("lk", "+94", "Sri Lanka", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("lr", "+231", "Liberia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ls", "+266", "Lesotho", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("lt", "+370", "Lithuania", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("lu", "+352", "Luxembourg", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("lv", "+371", "Latvia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ly", "+218", "Libya", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ma", "+212", "Morocco", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mc", "+377", "Monaco", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("md", "+373", "Moldova, Republic Of", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("me", "+382", "Montenegro", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mf", "+590", "Saint Martin", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mg", "+261", "Madagascar", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mh", "+692", "Marshall Islands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mk", "+389", "Macedonia, The Former Yugoslav Republic Of", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ml", "+223", "Mali", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mm", "+95", "Myanmar", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mn", "+976", "Mongolia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mo", "+853", "Macao", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mp", "+1", "Northern Mariana Islands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mq", "+596", "Martinique", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mr", "+222", "Mauritania", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ms", "+1", "Montserrat", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mt", "+356", "Malta", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mu", "+230", "Mauritius", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mv", "+960", "Maldives", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mw", "+265", "Malawi", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mx", "+52", "Mexico", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("my", "+60", "Malaysia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("mz", "+258", "Mozambique", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("na", "+264", "Namibia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("nc", "+687", "New Caledonia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ne", "+227", "Niger", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("nf", "+672", "Norfolk Islands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ng", "+234", "Nigeria", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ni", "+505", "Nicaragua", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("nl", "+31", "Netherlands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("no", "+47", "Norway", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("np", "+977", "Nepal", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("nr", "+674", "Nauru", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("nu", "+683", "Niue", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("nz", "+64", "New Zealand", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("om", "+968", "Oman", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("pa", "+507", "Panama", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("pe", "+51", "Peru", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("pf", "+689", "French Polynesia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("pg", "+675", "Papua New Guinea", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ph", "+63", "Philippines", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("pk", "+92", "Pakistan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("pl", "+48", "Poland", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("pm", "+508", "Saint Pierre And Miquelon", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("pn", "+870", "Pitcairn", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("pr", "+1", "Puerto Rico", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ps", "+970", "Palestine", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("pt", "+351", "Portugal", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("pw", "+680", "Palau", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("py", "+595", "Paraguay", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("qa", "+974", "Qatar", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("re", "+262", "Réunion", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ro", "+40", "Romania", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("rs", "+381", "Serbia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ru", "+7", "Russian Federation", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("rw", "+250", "Rwanda", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sa", "+966", "Saudi Arabia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sb", "+677", "Solomon Islands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sc", "+248", "Seychelles", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sd", "+249", "Sudan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("se", "+46", "Sweden", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sg", "+65", "Singapore", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sh", "+290", "Saint Helena, Ascension And Tristan Da Cunha", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("si", "+386", "Slovenia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sk", "+421", "Slovakia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sl", "+232", "Sierra Leone", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sm", "+378", "San Marino", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sn", "+221", "Senegal", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("so", "+252", "Somalia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sr", "+597", "Suriname", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("st", "+239", "Sao Tome And Principe", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sv", "+503", "El Salvador", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sx", "+1", "Sint Maarten", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sy", "+963", "Syrian Arab Republic", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("sz", "+268", "Swaziland", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("tc", "+1", "Turks and Caicos Islands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("td", "+235", "Chad", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("tg", "+228", "Togo", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("th", "+66", "Thailand", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("tj", "+992", "Tajikistan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("tk", "+690", "Tokelau", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("tl", "+670", "Timor-leste", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("tm", "+993", "Turkmenistan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("tn", "+216", "Tunisia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("to", "+676", "Tonga", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("tr", "+90", "Turkey", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("tt", "+1", "Trinidad &amp; Tobago", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("tv", "+688", "Tuvalu", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("tw", "+886", "Taiwan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("tz", "+255", "Tanzania, United Republic Of", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ua", "+380", "Ukraine", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ug", "+256", "Uganda", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("us", "+1", "United States", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("uy", "+598", "Uruguay", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("uz", "+998", "Uzbekistan", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("va", "+379", "Holy See (vatican City State)", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("vc", "+1", "Saint Vincent &amp; The Grenadines", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ve", "+58", "Venezuela, Bolivarian Republic Of", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("vg", "+1", "British Virgin Islands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("vi", "+1", "US Virgin Islands", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("vn", "+84", "Viet Nam", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("vu", "+678", "Vanuatu", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("wf", "+681", "Wallis And Futuna", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ws", "+685", "Samoa", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("xk", "+383", "Kosovo", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("ye", "+967", "Yemen", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("yt", "+262", "Mayotte", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("za", "+27", "South Africa", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("zm", "+260", "Zambia", DEFAULT_FLAG_RES));
        countries.add(new CountryCode("zw", "+263", "Zimbabwe", DEFAULT_FLAG_RES));        return countries;
    }

    @Override
    public int compareTo(@NonNull CountryCode o) {
        return Collator.getInstance().compare(getName(), o.getName());
    }
}
