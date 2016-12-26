# JCSG-MeshExtensions
[ ![Download](https://api.bintray.com/packages/miho/JCSG/JCSG-MeshExtensions/images/download.svg) ](https://bintray.com/miho/JCSG/JCSG-MeshExtensions/_latestVersion)

[JCSG](https://github.com/miho/JCSG) extension library for producing high-quality meshes from [JCSG](https://github.com/miho/JCSG) objects. The mesh optimization is performed with the cross-platform meshing software [ProMesh](http://promesh3d.com/) which is available as [ug4](https://github.com/UG4/ugcore) plugin.

<img src="https://raw.githubusercontent.com/miho/JCSG-MeshExtensions/master/res/img/optimize.jpg" alt="optimization" width="450">

## Sample Code
```java
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
```

## How to Build JCSG-MeshExtensions

### Requirements

- Java >= 1.8
- Internet connection (dependencies are downloaded automatically)
- IDE: [Gradle](http://www.gradle.org/) Plugin (not necessary for command line usage)

### IDE

Open the `JCSG-MeshExtensions` [Gradle](http://www.gradle.org/) project in your favourite IDE (tested with NetBeans 8.2) and build it
by calling the `assemble` task.

### Command Line

Navigate to the [Gradle](http://www.gradle.org/) project (e.g., `path/to/JCSG-MeshExtensions`) and enter the following command

#### Bash (Linux/OS X/Cygwin/other Unix-like shell)

    bash gradlew assemble
    
#### Windows (CMD)

    gradlew assemble
