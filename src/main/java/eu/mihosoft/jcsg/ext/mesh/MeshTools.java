/**
 * MeshTools.java
 *
 * Copyright 2016 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Michael Hoffer <info@michaelhoffer.de> "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL Michael Hoffer <info@michaelhoffer.de> OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of Michael Hoffer
 * <info@michaelhoffer.de>.
 */
package eu.mihosoft.jcsg.ext.mesh;

import eu.mihosoft.ugshell.vugshell.Shell;
import eu.mihosoft.jcsg.CSG;
import eu.mihosoft.jcsg.STL;
import eu.mihosoft.vvecmath.Transform;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Mesh tools for optimizing and manipulating csg mesh objects.
 *
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
public final class MeshTools {

    private MeshTools() {
        throw new AssertionError("Don't instantiate me!");
    }

    /**
     * Optimizes and repairs the specified csg mesh object.
     *
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
        return optimize(
                csg, tol, maxTol, minEdgeLength, maxEdgeLength, 10, 5.0);
    }

    /**
     * Optimizes and repairs the specified csg mesh object.
     *
     * @param csg csg to optimize
     * @param tol default tolerance
     * @param maxTol maximum tolerance
     * @param minEdgeLength minimum edge length
     * @param maxEdgeLength maximum edge length
     * @param maxIter number of iterations for edge length adjustment
     * @param creaseEdgeAngle angle threashold for crease edge marker
     * @return optimized csg mesh object
     */
    public static CSG optimize(
            CSG csg, double tol,
            double maxTol,
            double minEdgeLength,
            double maxEdgeLength,
            int maxIter,
            double creaseEdgeAngle) {
        try {

            Path tmpDir = Files.createTempDirectory("jcsgmeshopt");
            Path stlFile = Paths.get(tmpDir.toAbsolutePath().toString(),
                    "csg.stl");

            System.out.println("mesh-ext: csg file: " + stlFile);

            Files.write(stlFile, csg.toStlString().getBytes());

            String code = read("optimize-and-repair.lua");

            String pathVariable = stlFile.toAbsolutePath().toString();//

            if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                pathVariable = pathVariable.replace("\\", "\\\\");
            }

            code = code.replace("$filename$", "\""
                    + pathVariable + "\"");
            code = code.replace("$removeDoublesTOL$", "" + tol);
            code = code.replace("$creaseEdgeAngle$", "" + creaseEdgeAngle);
            code = code.replace("$resolveTOL$", "" + maxTol);
            code = code.replace("$minEdgeLength$", "" + minEdgeLength);
            code = code.replace("$maxEdgeLength$", "" + maxEdgeLength);
            code = code.replace("$maxAdjIter$", "" + maxIter);

//            code = code.replace("$edgeApprox$", "" + edgeApprox);
//            code = code.replace("$edgeTriangleQuality$", "" + edgeTriangleQuality);
            Shell.execute(tmpDir.toFile(), code).print().waitFor();

            return STL.file(stlFile);

        } catch (IOException e) {
            e.printStackTrace(System.err);
            throw new RuntimeException(
                    "optimization failed due to io exception", e);
        }
    }

    /**
     * Optimizes and repairs the specified csg mesh object.
     * 
     * <b>Note: </b>the size of the
     * object during optimization can have a high impact on the overall
     * optimization quality. Therefore, this method allows the specification of
     * the size at which the optimization is performed. After the optimization
     * the object is returned at original size.
     *
     * @param csg csg to optimize
     * @param size object size at which to perform the optimization (minimum
     * dimension)
     * @param tol default tolerance
     * @param maxTol maximum tolerance
     * @param minEdgeLength minimum edge length
     * @param maxEdgeLength maximum edge length
     * @return optimized csg mesh object
     */
    public static CSG optimize(
            CSG csg, double size, double tol,
            double maxTol,
            double minEdgeLength,
            double maxEdgeLength) {
        return scaleMinDimensionTo(csg, size,
                (csgObj) -> optimize(
                        csg, tol, maxTol,
                        minEdgeLength, maxEdgeLength,
                        10, 5.0));
    }

    /**
     * Optimizes and repairs the specified csg mesh object. 
     * 
     * <b>Note: </b>the size of the
     * object during optimization can have a high impact on the overall
     * optimization quality. Therefore, this method allows the specification of
     * the size at which the optimization is performed. After the optimization
     * the object is returned at original size.
     *
     * @param csg csg to optimize
     * @param size object size at which to perform the optimization (minimum
     * dimension)
     * @param tol default tolerance
     * @param maxTol maximum tolerance
     * @param minEdgeLength minimum edge length
     * @param maxEdgeLength maximum edge length
     * @param maxIter number of iterations for edge length adjustment
     * @param creaseEdgeAngle angle threashold for crease edge marker
     * @return optimized csg mesh object
     */
    public static CSG optimize(CSG csg,
            double size,
            double tol,
            double maxTol,
            double minEdgeLength,
            double maxEdgeLength,
            int maxIter,
            double creaseEdgeAngle) {
        return scaleMinDimensionTo(csg, size,
                (csgObj) -> optimize(csgObj, tol, maxTol,
                        minEdgeLength, maxEdgeLength));
    }

    /**
     * Scales the minimum CSG dimension to the specified value, invokes the
     * specified function and rescales the specified CSG object to its original
     * size.
     *
     * @param csg csg to process at specified scale
     * @param scale scale
     * @param processF processing function
     * @return the processed CSG at original scale
     */
    private static CSG scaleMinDimensionTo(CSG csg,
            double scale, Function<CSG, CSG> processF) {

        double w = csg.getBounds().getBounds().getX();
        double h = csg.getBounds().getBounds().getY();
        double d = csg.getBounds().getBounds().getZ();

        // find minimum dimension
        double size = Math.min(w, Math.min(h, d));

        // scale CSG object so its minimum dimension is 100 in size
        CSG result = csg;
        double scale1 = 1.0 / size * scale;
        double scale2 = 1.0 / scale1;
        result = result.transformed(Transform.unity().scale(scale1));

        result = processF.apply(result);

        // restore original scale
        result = result.transformed(Transform.unity().scale(scale2));

        return result;
    }

    private static String read(String resourceName) {
        return new Scanner(MeshTools.class.getResourceAsStream(resourceName),
                "UTF-8").useDelimiter("\\A").next();
    }
}
