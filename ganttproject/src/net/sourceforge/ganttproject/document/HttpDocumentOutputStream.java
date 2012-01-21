/*
GanttProject is an opensource project management tool.
Copyright (C) 2003-2011 GanttProject team

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 3
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
*/
package net.sourceforge.ganttproject.document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.ws.http.HTTPException;

import org.apache.webdav.lib.WebdavResource;

/**
 * This class implements an OutputStream for documents on
 * WebDAV-enabled-servers. It is a helper class for HttpDocument.
 *
 * @see HttpDocument
 * @author Michael Haeusler (michael at akatose.de)
 */
class HttpDocumentOutputStream extends ByteArrayOutputStream {

    private final HttpDocument myDocument;

    HttpDocumentOutputStream(HttpDocument document) {
        super();
        myDocument = document;
    }

    @Override
    public void close() throws IOException {
        super.close();
        WebdavResource wr = myDocument.getWebdavResource();
        wr.lockMethod(myDocument.getUsername(), 60);
        try {
            if (!wr.putMethod(toByteArray())) {
                throw new IOException("Failed to write data: " + wr.getStatusMessage());
            }
        } catch (HTTPException e) {
            throw new IOException("Code: " + e.getStatusCode());
        } finally {
            wr.unlockMethod();
        }
    }
}
