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
//package open.dolphin.toucha;
//
//import java.awt.Dimension;
//import java.awt.Graphics2D;
//import java.awt.RenderingHints;
//import java.awt.geom.AffineTransform;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import javax.imageio.ImageIO;
//
///**
// * ImageTool
// *
// * @author masuda, Masuda Naika
// * @author modified Chikara Fujihira <fujihirach@sandi.co.jp>, S&I Co.,Ltd.
// */
//public class ImageTool {
//
//    public static byte[] getScaledBytes(byte[] bytes, Dimension dim, String format) {
//        try {
//            BufferedImage src = ImageIO.read(new ByteArrayInputStream(bytes));
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            ImageIO.write(getFirstScaledInstance(src, dim), format, bos);
//            return bos.toByteArray();
//        } catch (IOException ex) {
//        }
//        return null;
//    }
//
//    private static BufferedImage getFirstScaledInstance(BufferedImage inImage, Dimension dim) {
//
//        if (inImage.getWidth() <= dim.width && inImage.getHeight() <= dim.height) {
//            return inImage;
//        }
//
//        // Determine the scale.
//        double scaleH = (double) dim.height / (double) inImage.getHeight();
//        double scaleW = (double) dim.width / (double) inImage.getWidth();
//        double scale = Math.min(scaleH, scaleW);
//
//        // Determine size of new image.
//        int scaledW = (int) (scale * (double) inImage.getWidth());
//        int scaledH = (int) (scale * (double) inImage.getHeight());
//
//        // Create an image buffer in which to paint on.
//        BufferedImage outImage = new BufferedImage(scaledW, scaledH, BufferedImage.TYPE_INT_BGR);
//
//        // Set the scale.
//        AffineTransform tx = new AffineTransform();
//
//        // If the image is smaller than the desired image size,
//        if (scale < 1.0d) {
//            tx.scale(scale, scale);
//        }
//
//        // Paint image.
//        Graphics2D g2d = outImage.createGraphics();
//        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//        g2d.drawImage(inImage, tx, null);
//        g2d.dispose();
//
//        return outImage;
//    }
//}
