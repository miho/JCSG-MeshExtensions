/**
 * MeshTools.java
 *
 * Copyright 2016 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and should not be interpreted as representing official policies, either expressed
 * or implied, of Michael Hoffer <info@michaelhoffer.de>.
 */ 

package eu.mihosoft.jcsg.ext.mesh;

import eu.mihosoft.ugshell.vugshell.Shell;
import eu.mihosoft.vrl.v3d.CSG;
import eu.mihosoft.vrl.v3d.STL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Mesh tools for optimizing and manipulating csg mesh objects.
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public class MeshTools {

    /**
     * Optimizes and repairs the specified csg mesh object.
     * @param csg csg to optimize
     * @param tol default tolerance
     * @param maxTol maximum tolerance
     * @param minEdgeLength minimum edge length
     * @param maxEdgeLength maximum edge length
     * @return optimized csg mesh object
     */
    public static CSG optimize(
            CSG csg, double tol,
            double maxTol,
            double minEdgeLength,
            double maxEdgeLength) {
        try {
            
            Path tmpDir = Files.createTempDirectory("jcsgmeshopt");
            Path stlFile = Paths.get(tmpDir.toAbsolutePath().toString(),
                    "csg.stl");

            Files.write(stlFile, csg.toStlString().getBytes());

            String code = read("optimize-and-repair.lua");

            code = code.replace("$filename$", "\""
                    + stlFile.toAbsolutePath().toString() + "\"");
            code = code.replace("$removeDoublesTOL$", "" + tol);
            code = code.replace("$creaseEdgeAngle$", "5.0");
            code = code.replace("$resolveTOL$", "" + maxTol);
            code = code.replace("$minEdgeLength$", "" + minEdgeLength);
            code = code.replace("$maxEdgeLength$", "" + maxEdgeLength);
            code = code.replace("$maxAdjIter$", "10");

            Shell.execute(tmpDir.toFile(), code).print().waitFor();

            return STL.file(stlFile);

        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(
                    "optimization failed due to io exception", e);
        }
    }

    private static String read(String resourceName) {
        return new Scanner(MeshTools.class.getResourceAsStream(resourceName),
                "UTF-8").useDelimiter("\\A").next();
    }
}
