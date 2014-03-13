/*
 * Copyright (C) 2014 S&I Co.,Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp.
 * 825 Sylk BLDG., 1-Yamashita-Cho, Naka-Ku, Kanagawa-Ken, Yokohama-City, JAPAN.
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the 
 * GNU General Public License as published by the Free Software Foundation; either version 3 
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR 
 * PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 
 * 02111-1307 USA.
 * 
 * (R)OpenDolphin version 2.4, Copyright (C) 2001-2014 OpenDolphin Lab., Life Sciences Computing, Corp. 
 * (R)OpenDolphin comes with ABSOLUTELY NO WARRANTY; for details see the GNU General 
 * Public License, version 3 (GPLv3) This is free software, and you are welcome to redistribute 
 * it under certain conditions; see the GPLv3 for details.
 */
package open.dolphin.mbean;

import java.util.*;

/**
 * ConvData
 *
 * @author ishizaka
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
class ConvData {

    String m_sAsc;
    String m_sKana;

    ConvData(String strAC, String strKana) {
        m_sAsc = strAC;
        m_sKana = strKana;
    }

    String GetAsc() {
        return m_sAsc;
    }

    String GetKana() {
        return m_sKana;
    }
}

public class KanaToAscii {

    List<ConvData> m_DataAry;

    public KanaToAscii() {
        m_DataAry = new ArrayList<ConvData>();
        ConvData pData;
        pData = new ConvData("A", "ア");
        m_DataAry.add(pData);
        pData = new ConvData("I", "イ");
        m_DataAry.add(pData);
        pData = new ConvData("U", "ウ");
        m_DataAry.add(pData);
        pData = new ConvData("E", "エ");
        m_DataAry.add(pData);
        pData = new ConvData("O", "オ");
        m_DataAry.add(pData);
        pData = new ConvData("KA", "カ");
        m_DataAry.add(pData);
        pData = new ConvData("KI", "キ");
        m_DataAry.add(pData);
        pData = new ConvData("KU", "ク");
        m_DataAry.add(pData);
        pData = new ConvData("KE", "ケ");
        m_DataAry.add(pData);
        pData = new ConvData("KO", "コ");
        m_DataAry.add(pData);
        pData = new ConvData("SA", "サ");
        m_DataAry.add(pData);
        pData = new ConvData("SHI", "シ");
        m_DataAry.add(pData);
        pData = new ConvData("SU", "ス");
        m_DataAry.add(pData);
        pData = new ConvData("SE", "セ");
        m_DataAry.add(pData);
        pData = new ConvData("SO", "ソ");
        m_DataAry.add(pData);
        pData = new ConvData("TA", "タ");
        m_DataAry.add(pData);
        pData = new ConvData("CHI", "チ");
        m_DataAry.add(pData);
        pData = new ConvData("TSU", "ツ");
        m_DataAry.add(pData);
        pData = new ConvData("TE", "テ");
        m_DataAry.add(pData);
        pData = new ConvData("TO", "ト");
        m_DataAry.add(pData);
        pData = new ConvData("NA", "ナ");
        m_DataAry.add(pData);
        pData = new ConvData("NI", "ニ");
        m_DataAry.add(pData);
        pData = new ConvData("NU", "ヌ");
        m_DataAry.add(pData);
        pData = new ConvData("NE", "ネ");
        m_DataAry.add(pData);
        pData = new ConvData("NO", "ノ");
        m_DataAry.add(pData);
        pData = new ConvData("HA", "ハ");
        m_DataAry.add(pData);
        pData = new ConvData("HI", "ヒ");
        m_DataAry.add(pData);
        //pData = new ConvData( "HU", "ﾌ" );			m_DataAry.add( pData );
        pData = new ConvData("FU", "フ");
        m_DataAry.add(pData);	// chg 07.10.19 K.Funabashi
        pData = new ConvData("HE", "へ");
        m_DataAry.add(pData);
        pData = new ConvData("HO", "ホ");
        m_DataAry.add(pData);
        pData = new ConvData("MA", "マ");
        m_DataAry.add(pData);
        pData = new ConvData("MI", "ミ");
        m_DataAry.add(pData);
        pData = new ConvData("MU", "ム");
        m_DataAry.add(pData);
        pData = new ConvData("ME", "メ");
        m_DataAry.add(pData);
        pData = new ConvData("MO", "モ");
        m_DataAry.add(pData);
        pData = new ConvData("YA", "ヤ");
        m_DataAry.add(pData);
        pData = new ConvData("YU", "ユ");
        m_DataAry.add(pData);
        pData = new ConvData("YO", "ヨ");
        m_DataAry.add(pData);
        pData = new ConvData("RA", "ラ");
        m_DataAry.add(pData);
        pData = new ConvData("RI", "リ");
        m_DataAry.add(pData);
        pData = new ConvData("RU", "ル");
        m_DataAry.add(pData);
        pData = new ConvData("RE", "レ");
        m_DataAry.add(pData);
        pData = new ConvData("RO", "ロ");
        m_DataAry.add(pData);
        pData = new ConvData("WA", "ワ");
        m_DataAry.add(pData);
        pData = new ConvData("WO", "ヲ");
        m_DataAry.add(pData);
        pData = new ConvData("N", "ン");
        m_DataAry.add(pData);
        //pData = new ConvData( "TTSU", "ｯ" );		m_DataAry.add( pData );
        pData = new ConvData("$", "ｯ");
        m_DataAry.add(pData);	// chg 07.10.19 K.Funabashi ヘボン式対応
        pData = new ConvData("KWA", "クァ");
        m_DataAry.add(pData);
        pData = new ConvData("KWI", "クィ");
        m_DataAry.add(pData);
        pData = new ConvData("KWU", "クゥ");
        m_DataAry.add(pData);
        pData = new ConvData("KWE", "クェ");
        m_DataAry.add(pData);
        pData = new ConvData("KWO", "クォ");
        m_DataAry.add(pData);

        pData = new ConvData("FA", "ファ");
        m_DataAry.add(pData);
        pData = new ConvData("FI", "フィ");
        m_DataAry.add(pData);
        pData = new ConvData("FU", "フゥ");
        m_DataAry.add(pData);
        pData = new ConvData("FE", "フェ");
        m_DataAry.add(pData);
        pData = new ConvData("FO", "フォ");
        m_DataAry.add(pData);

        pData = new ConvData("FYA", "フャ");
        m_DataAry.add(pData);
        pData = new ConvData("FYU", "フュ");
        m_DataAry.add(pData);
        pData = new ConvData("FYO", "フョ");
        m_DataAry.add(pData);

        pData = new ConvData("KYA", "キャ");
        m_DataAry.add(pData);
        pData = new ConvData("KYI", "キィ");
        m_DataAry.add(pData);
        pData = new ConvData("KYU", "キュ");
        m_DataAry.add(pData);
        pData = new ConvData("KYE", "キェ");
        m_DataAry.add(pData);
        pData = new ConvData("KYO", "キョ");
        m_DataAry.add(pData);

        pData = new ConvData("SHA", "シャ");
        m_DataAry.add(pData);
        pData = new ConvData("SHU", "シュ");
        m_DataAry.add(pData);
        pData = new ConvData("SHE", "シェ");
        m_DataAry.add(pData);
        pData = new ConvData("SHO", "ショ");
        m_DataAry.add(pData);
        //pData = new ConvData( "TYA", "ﾁｬ" );		m_DataAry.add( pData );
        pData = new ConvData("CHA", "チャ");
        m_DataAry.add(pData);	// chg 07.10.19 K.Funabashi ヘボン式対応
        pData = new ConvData("TYI", "チィ");
        m_DataAry.add(pData);
        //pData = new ConvData( "TYU", "ﾁｭ" );		m_DataAry.add( pData );
        pData = new ConvData("CHU", "チュ");
        m_DataAry.add(pData);	// chg 07.10.19 K.Funabashi ヘボン式対応
        pData = new ConvData("TYE", "チェ");
        m_DataAry.add(pData);
        //pData = new ConvData( "CYO", "ﾁｮ" );		m_DataAry.add( pData );
        pData = new ConvData("CHO", "チヨ");
        m_DataAry.add(pData);	// chg 07.10.19 K.Funabashi ヘボン式対応

        pData = new ConvData("NYA", "ニャ");
        m_DataAry.add(pData);
        pData = new ConvData("NYI", "ニィ");
        m_DataAry.add(pData);
        pData = new ConvData("NYU", "ニュ");
        m_DataAry.add(pData);
        pData = new ConvData("NYE", "ニェ");
        m_DataAry.add(pData);
        pData = new ConvData("NYO", "ニョ");
        m_DataAry.add(pData);

        pData = new ConvData("HYA", "ヒャ");
        m_DataAry.add(pData);
        pData = new ConvData("HYI", "ヒィ");
        m_DataAry.add(pData);
        pData = new ConvData("HYU", "ヒュ");
        m_DataAry.add(pData);
        pData = new ConvData("HYE", "ヒェ");
        m_DataAry.add(pData);
        pData = new ConvData("HYO", "ヒョ");
        m_DataAry.add(pData);

        pData = new ConvData("MYA", "ミャ");
        m_DataAry.add(pData);
        pData = new ConvData("MYI", "ミィ");
        m_DataAry.add(pData);
//  pData = new ConvData( "MY",  "ﾐｭ" );		m_DataAry.add( pData );
        pData = new ConvData("MYU", "ミュ");
        m_DataAry.add(pData); // 00.12.06 ﾀｲﾌﾟﾐｽ?
        pData = new ConvData("MYE", "ミェ");
        m_DataAry.add(pData);
        pData = new ConvData("MYO", "ミョ");
        m_DataAry.add(pData);

        pData = new ConvData("RYA", "リャ");
        m_DataAry.add(pData);
        pData = new ConvData("RYI", "リィ");
        m_DataAry.add(pData);
        pData = new ConvData("RYU", "リュ");
        m_DataAry.add(pData);
        pData = new ConvData("RYE", "リェ");
        m_DataAry.add(pData);
        pData = new ConvData("RYO", "リョ");
        m_DataAry.add(pData);

        pData = new ConvData("WI", "ウィ");
        m_DataAry.add(pData);
        pData = new ConvData("WE", "ウェ");
        m_DataAry.add(pData);

        pData = new ConvData("GA", "ガ");
        m_DataAry.add(pData);
        pData = new ConvData("GI", "ギ");
        m_DataAry.add(pData);
        pData = new ConvData("GU", "グ");
        m_DataAry.add(pData);
        pData = new ConvData("VU", "ｳﾞ");
        m_DataAry.add(pData);
        pData = new ConvData("GE", "ゲ");
        m_DataAry.add(pData);
        pData = new ConvData("GO", "ゴ");
        m_DataAry.add(pData);
        pData = new ConvData("ZA", "ザ");
        m_DataAry.add(pData);
        pData = new ConvData("JI", "ジ");
        m_DataAry.add(pData);
        pData = new ConvData("ZU", "ズ");
        m_DataAry.add(pData);
        pData = new ConvData("ZE", "ゼ");
        m_DataAry.add(pData);
        pData = new ConvData("ZO", "ゾ");
        m_DataAry.add(pData);
        pData = new ConvData("DA", "ダ");
        m_DataAry.add(pData);
        pData = new ConvData("DI", "ヂ");
        m_DataAry.add(pData);
        pData = new ConvData("DU", "ヅ");
        m_DataAry.add(pData);
        pData = new ConvData("DE", "デ");
        m_DataAry.add(pData);
        pData = new ConvData("DO", "ド");
        m_DataAry.add(pData);
        pData = new ConvData("BA", "バ");
        m_DataAry.add(pData);
        pData = new ConvData("BI", "ビ");
        m_DataAry.add(pData);
        pData = new ConvData("BU", "ブ");
        m_DataAry.add(pData);
        pData = new ConvData("BE", "ベ");
        m_DataAry.add(pData);
        pData = new ConvData("BO", "ボ");
        m_DataAry.add(pData);
        pData = new ConvData("PA", "パ");
        m_DataAry.add(pData);
        pData = new ConvData("PI", "ピ");
        m_DataAry.add(pData);
        pData = new ConvData("PU", "プ");
        m_DataAry.add(pData);
        pData = new ConvData("PE", "ペ");
        m_DataAry.add(pData);
        pData = new ConvData("PO", "ポ");
        m_DataAry.add(pData);
        pData = new ConvData("GWA", "グァ");
        m_DataAry.add(pData);
        pData = new ConvData("GWI", "グィ");
        m_DataAry.add(pData);
        pData = new ConvData("GWU", "グゥ");
        m_DataAry.add(pData);
        pData = new ConvData("GWE", "グェ");
        m_DataAry.add(pData);
        pData = new ConvData("GWO", "グォ");
        m_DataAry.add(pData);

        pData = new ConvData("GYA", "ギャ");
        m_DataAry.add(pData);
        pData = new ConvData("GYI", "ギィ");
        m_DataAry.add(pData);
        pData = new ConvData("GYU", "ギュ");
        m_DataAry.add(pData);
        pData = new ConvData("GYE", "ギェ");
        m_DataAry.add(pData);
        pData = new ConvData("GYO", "ギョ");
        m_DataAry.add(pData);

        pData = new ConvData("JA", "ジャ");
        m_DataAry.add(pData);
        pData = new ConvData("ZYI", "ジィ");
        m_DataAry.add(pData);
        pData = new ConvData("JU", "ジュ");
        m_DataAry.add(pData);
        pData = new ConvData("JE", "ジェ");
        m_DataAry.add(pData);
        pData = new ConvData("JO", "ジョ");
        m_DataAry.add(pData);

        pData = new ConvData("DYA", "ヂァ");
        m_DataAry.add(pData);
        pData = new ConvData("DYI", "ヂィ");
        m_DataAry.add(pData);
        pData = new ConvData("DYU", "ヂョ");
        m_DataAry.add(pData);
        pData = new ConvData("DYE", "ヂェ");
        m_DataAry.add(pData);
        pData = new ConvData("DYO", "ヂョ");
        m_DataAry.add(pData);

        pData = new ConvData("BYA", "ビャ");
        m_DataAry.add(pData);
        pData = new ConvData("BYI", "ビィ");
        m_DataAry.add(pData);
        pData = new ConvData("BYU", "ビュ");
        m_DataAry.add(pData);
        pData = new ConvData("BYE", "ビェ");
        m_DataAry.add(pData);
        pData = new ConvData("BYO", "ビョ");
        m_DataAry.add(pData);

        pData = new ConvData("VA", "ヴァ");
        m_DataAry.add(pData);
        pData = new ConvData("VI", "ヴィ");
        m_DataAry.add(pData);
        pData = new ConvData("VE", "ヴェ");
        m_DataAry.add(pData);
        pData = new ConvData("VO", "ヴォ");
        m_DataAry.add(pData);
        pData = new ConvData("VYA", "ヴャ");
        m_DataAry.add(pData);
        pData = new ConvData("VYI", "ヴｨ");
        m_DataAry.add(pData);
        pData = new ConvData("VYU", "ヴュ");
        m_DataAry.add(pData);
        pData = new ConvData("VYE", "ヴェ");
        m_DataAry.add(pData);
        pData = new ConvData("VYO", "ヴョ");
        m_DataAry.add(pData);

        pData = new ConvData("PYA", "ピャ");
        m_DataAry.add(pData);
        pData = new ConvData("PYI", "ピィ");
        m_DataAry.add(pData);
        pData = new ConvData("PYU", "ピュ");
        m_DataAry.add(pData);
        pData = new ConvData("PYE", "ピェ");
        m_DataAry.add(pData);
        pData = new ConvData("PYO", "ペョ");
        m_DataAry.add(pData);

        pData = new ConvData(" ", " ");
        m_DataAry.add(pData);
        pData = new ConvData("", "ｰ");
        m_DataAry.add(pData);	// chg 07.10.19 K.Funabashi 念のため

        pData = new ConvData("0", "０");
        m_DataAry.add(pData);
        pData = new ConvData("1", "１");
        m_DataAry.add(pData);
        pData = new ConvData("2", "２");
        m_DataAry.add(pData);
        pData = new ConvData("3", "３");
        m_DataAry.add(pData);
        pData = new ConvData("4", "４");
        m_DataAry.add(pData);
        pData = new ConvData("5", "５");
        m_DataAry.add(pData);
        pData = new ConvData("6", "６");
        m_DataAry.add(pData);
        pData = new ConvData("7", "７");
        m_DataAry.add(pData);
        pData = new ConvData("8", "８");
        m_DataAry.add(pData);
        pData = new ConvData("9", "９");
        m_DataAry.add(pData);
        pData = new ConvData("A", "Ａ");
        m_DataAry.add(pData);
        pData = new ConvData("B", "Ｂ");
        m_DataAry.add(pData);
        pData = new ConvData("C", "Ｃ");
        m_DataAry.add(pData);
        pData = new ConvData("D", "Ｄ");
        m_DataAry.add(pData);
        pData = new ConvData("E", "Ｅ");
        m_DataAry.add(pData);
        pData = new ConvData("F", "Ｆ");
        m_DataAry.add(pData);
        pData = new ConvData("G", "Ｇ");
        m_DataAry.add(pData);
        pData = new ConvData("H", "Ｈ");
        m_DataAry.add(pData);
        pData = new ConvData("I", "Ｉ");
        m_DataAry.add(pData);
        pData = new ConvData("J", "Ｊ");
        m_DataAry.add(pData);
        pData = new ConvData("K", "Ｋ");
        m_DataAry.add(pData);
        pData = new ConvData("L", "Ｌ");
        m_DataAry.add(pData);
        pData = new ConvData("M", "Ｍ");
        m_DataAry.add(pData);
        pData = new ConvData("N", "Ｎ");
        m_DataAry.add(pData);
        pData = new ConvData("O", "Ｏ");
        m_DataAry.add(pData);
        pData = new ConvData("P", "Ｐ");
        m_DataAry.add(pData);
        pData = new ConvData("Q", "Ｑ");
        m_DataAry.add(pData);
        pData = new ConvData("R", "Ｒ");
        m_DataAry.add(pData);
        pData = new ConvData("S", "Ｓ");
        m_DataAry.add(pData);
        pData = new ConvData("T", "Ｔ");
        m_DataAry.add(pData);
        pData = new ConvData("U", "Ｕ");
        m_DataAry.add(pData);
        pData = new ConvData("V", "Ｖ");
        m_DataAry.add(pData);
        pData = new ConvData("W", "Ｗ");
        m_DataAry.add(pData);
        pData = new ConvData("X", "Ｘ");
        m_DataAry.add(pData);
        pData = new ConvData("Y", "Ｙ");
        m_DataAry.add(pData);
        pData = new ConvData("Z", "Ｚ");
        m_DataAry.add(pData);
        pData = new ConvData("a", "ａ");
        m_DataAry.add(pData);
        pData = new ConvData("b", "ｂ");
        m_DataAry.add(pData);
        pData = new ConvData("c", "ｃ");
        m_DataAry.add(pData);
        pData = new ConvData("d", "ｄ");
        m_DataAry.add(pData);
        pData = new ConvData("e", "ｅ");
        m_DataAry.add(pData);
        pData = new ConvData("f", "ｆ");
        m_DataAry.add(pData);
        pData = new ConvData("g", "ｇ");
        m_DataAry.add(pData);
        pData = new ConvData("h", "ｈ");
        m_DataAry.add(pData);
        pData = new ConvData("i", "ｉ");
        m_DataAry.add(pData);
        pData = new ConvData("j", "ｊ");
        m_DataAry.add(pData);
        pData = new ConvData("k", "ｋ");
        m_DataAry.add(pData);
        pData = new ConvData("l", "ｌ");
        m_DataAry.add(pData);
        pData = new ConvData("m", "ｍ");
        m_DataAry.add(pData);
        pData = new ConvData("n", "ｎ");
        m_DataAry.add(pData);
        pData = new ConvData("o", "ｏ");
        m_DataAry.add(pData);
        pData = new ConvData("p", "ｐ");
        m_DataAry.add(pData);
        pData = new ConvData("q", "ｑ");
        m_DataAry.add(pData);
        pData = new ConvData("r", "ｒ");
        m_DataAry.add(pData);
        pData = new ConvData("s", "ｓ");
        m_DataAry.add(pData);
        pData = new ConvData("t", "ｔ");
        m_DataAry.add(pData);
        pData = new ConvData("u", "ｕ");
        m_DataAry.add(pData);
        pData = new ConvData("v", "ｖ");
        m_DataAry.add(pData);
        pData = new ConvData("w", "ｗ");
        m_DataAry.add(pData);
        pData = new ConvData("x", "ｘ");
        m_DataAry.add(pData);
        pData = new ConvData("y", "ｙ");
        m_DataAry.add(pData);
        pData = new ConvData("z", "ｚ");
        m_DataAry.add(pData);
    }

    void KanaToAscii_Free() {
        m_DataAry.clear();
    }

    int GetSize() {
        return m_DataAry.size();
    }

    String GetAsc(int nPos) {
        if (nPos < 0 || nPos > GetSize() - 1) {
//		return CString( "" );
            String sRet = null;
            return sRet; // 02.05.31 kin
        }
        return m_DataAry.get(nPos).GetAsc();
    }

    String GetKana(int nPos) {
        if (nPos < 0 || nPos > GetSize() - 1) {
//		return CString( "" );
            String sRet = null;
            return sRet; // 02.05.31 kin
        }
        return m_DataAry.get(nPos).GetKana();
    }

    public String CHGKanatoASCII(String strKana, String strASCII) {

        String strCHG = "";
        boolean bTrue = false;
        int nLen = 0;
        ConvData dic;

        for (nLen = 0; nLen < strKana.length(); nLen++) {
            String strSub = "";
            bTrue = false;
            if (nLen + 1 < strKana.length()) {
                strSub = strKana.substring((nLen), nLen + 2);
                for (Iterator i = m_DataAry.iterator(); i.hasNext();) {
                    dic = (ConvData) i.next();
                    if (dic.m_sKana.equals(strSub)) {
                        strCHG += dic.m_sAsc;
                        bTrue = true;
                        break;
                    }
                }
                if (bTrue) {
                    nLen += 1;
                    continue;
                }

            }
            strSub = strKana.substring((nLen), nLen + 1);
            for (Iterator i = m_DataAry.iterator(); i.hasNext();) {
                dic = (ConvData) i.next();
                if (dic.m_sKana.equals(strSub)) {
                    strCHG += dic.m_sAsc;
                    bTrue = true;
                    break;
                }
            }
            if (!bTrue) {
                //return -1;
                //return null;
                // カナ/英数字以外の文字は?に変換する
                strCHG += "?";
            }

        }
        byte[] ChgByte = strCHG.getBytes();
        for (nLen = 0; nLen < ChgByte.length; nLen++) {
            if (ChgByte[nLen] == '$') {
                if (nLen + 1 < ChgByte.length) {
                    if (ChgByte[nLen + 1] != ' ') {
                        ChgByte[nLen] = ChgByte[nLen + 1];
                        if (ChgByte[nLen] == 'C') {
                            ChgByte[nLen] = 'T';
                        }
                    }
                }
            } else if (ChgByte[nLen] == 'N' && nLen + 1 < ChgByte.length) {
                if (ChgByte[nLen + 1] == 'B' || ChgByte[nLen + 1] == 'M' || ChgByte[nLen + 1] == 'P') {
                    ChgByte[nLen] = 'M';
                }
            }
        }
        String sAsc = new String(ChgByte); //    

        sAsc.replace("$", "");
        sAsc.replace("OO", "OH");
        sAsc.replace("OU", "OH");
        sAsc.replace("AA", "A");
        sAsc.replace("II", "I");
        sAsc.replace("UU", "U");
        sAsc.replace("EE", "E");

        //strASCII = sAsc;
        return sAsc;
    }

    public static void main(String[] args) {
        KanaToAscii ka = new KanaToAscii();
        String result = ka.CHGKanatoASCII("フナバシ ケンジ", "");
        System.err.println(result);
    }
}
