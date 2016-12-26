package eu.mihosoft.jcsg.ext.mesh;

import eu.mihosoft.vrl.v3d.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        // outer cyl radius
        double or = 10;
        // outer cyl height
        double oh = 5;
        // outer cyl resolution
        int ores = 64;

        // outer cylinder (surface)
        CSG outerCyl = new Cylinder(or, oh, ores).toCSG();
//        FileUtil.write(Paths.get("cyl-surface.stl"), outerCyl.toStlString());

        // wall thickness (outer cyl - inner cyl)
        double wallThickness = 5;
        // thickness of bottom cyl
        double bottomThickness = 1;

        // resolution of inner and bottom cyl
        int ires = ores;

        // inner cyl with hole
        CSG hole = new Cylinder(or - wallThickness, oh, ires).toCSG();
        CSG innerCyl = outerCyl.difference(hole);
//        FileUtil.write(Paths.get("cyl-inner.stl"), innerCyl.toStlString());

        // inner cyl bottom
        CSG innerCylBottom
                = new Cylinder(or - wallThickness,
                        bottomThickness, ires).toCSG();
//        FileUtil.write(Paths.get("cyl-bottom.stl"),
//          innerCylBottom.toStlString());

        innerCylBottom = innerCylBottom.
                transformed(Transform.unity().translateZ(0.5));

        // all as obj file
        CSG all = innerCyl.union(innerCylBottom);
        Files.write(Paths.get("all.stl"), all.toStlString().getBytes());

        all = MeshTools.optimize(
                STL.file(Paths.get("all.stl")),
                1e-6, 1e-4, 1, 2);

        Files.write(Paths.get("all.stl"), all.toStlString().getBytes());

    }
}
