package eu.mihosoft.jcsg.ext.mesh;

import eu.mihosoft.vrl.v3d.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) throws IOException {

        // we use cube and sphere as base geometries
        CSG cube   = new Cube(2).toCSG();
        CSG sphere = new Sphere(1.25).toCSG();
        
        // compute difference between cube and sphere
        CSG cubeMinusSphere = cube.difference(sphere);
        
        // create a copy of cube-sphere that shall be optimized
        CSG optimized       = cubeMinusSphere.
                transformed(Transform.unity().translateX(3));

        // perform the optimization
        CSG all = MeshTools.optimize(
                optimized, // csg object to optimize
                1e-6,      // tolerance
                1e-4,      // max tolerance
                0.25,      // min edge length
                1.5        // max edge length
        );

        // save optimized mesh as "all.stl"
        Files.write(Paths.get("all.stl"), all.toStlString().getBytes());
    }
}
