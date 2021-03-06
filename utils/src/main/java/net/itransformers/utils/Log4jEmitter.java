/*
 * Log4jEmitter.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.utils;/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import net.sf.saxon.event.Emitter;
import net.sf.saxon.trans.XPathException;
import org.apache.log4j.Logger;

public class Log4jEmitter extends Emitter {
    Logger logger = Logger.getLogger(Log4jEmitter.class.getName());
    @Override
    public void open() throws XPathException { }

    @Override
    public void startDocument(int i) throws XPathException { }

    @Override
    public void endDocument() throws XPathException { }

    @Override
    public void startElement(int i, int i1, int i2, int i3) throws XPathException { }

    @Override
    public void namespace(int i, int i1) throws XPathException { }

    @Override
    public void attribute(int i, int i1, CharSequence charSequence, int i2, int i3) throws XPathException { }

    @Override
    public void startContent() throws XPathException {   }

    @Override
    public void endElement() throws XPathException { }

    @Override
    public void characters(CharSequence charSequence, int i, int i1) throws XPathException {
        String msg = charSequence.toString();
        int idx = msg.indexOf(":");
        if (idx == -1) {
            logger.debug(msg);
        } else {
            String levelStr = msg.substring(0, idx);
            msg = msg.substring(idx+1);
            if (levelStr.equals("INFO")){
                logger.info(msg);
            } else if (levelStr.equals("DEBUG")) {
                logger.debug(msg);
            } else if (levelStr.equals("ERROR")) {
                logger.error(msg);
            } else if (levelStr.equals("WARN")) {
                logger.warn(msg);
            } else if (levelStr.equals("TRACE")) {
                logger.trace(msg);
            } else if (levelStr.equals("FATAL")) {
                logger.fatal(msg);
            } else {
                logger.debug(msg);
            }
        }
    }

    @Override
    public void processingInstruction(String s, CharSequence charSequence, int i, int i1) throws XPathException { }

    @Override
    public void comment(CharSequence charSequence, int i, int i1) throws XPathException { }

    @Override
    public void close() throws XPathException { }

}
