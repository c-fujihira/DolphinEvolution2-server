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
package open.dolphin.msg;

/**
 * ZenkakuUtils
 *
 * @author Kazushi Minagawa.
 * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
 */
public class ZenkakuUtils {

    private static final char FULL_MINUS = (char) 65293;
    private static final char HALF_MINUS = '-';

    private static final char[] MATCHIES = {'０', '１', '２', '３', '４', '５', '６', '７', '８', '９', '　', 'ｍ', 'ｇ', 'Ｌ', '．', '＋', FULL_MINUS};
    private static final char[] REPLACES = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', 'm', 'g', 'L', '.', '+', HALF_MINUS};

    public static String toHalfNumber(String test) {
        if (test != null) {
            for (int i = 0; i < MATCHIES.length; i++) {
                test = test.replace(MATCHIES[i], REPLACES[i]);
            }
        }
        return test;
    }

    // UTF-8
    // コメント等の全角のマイナスを半角へ変換する
    public static String utf8Replace(String str) {
        if (str != null) {
            return str.replace(FULL_MINUS, HALF_MINUS);
        }
        return null;
    }
}
