module com.tiemens.secretshare {
    requires java.base;
    requires transitive java.logging;

    exports com.tiemens.secretshare;
    exports com.tiemens.secretshare.engine;
    exports com.tiemens.secretshare.exceptions;
    exports com.tiemens.secretshare.main.cli;
    exports com.tiemens.secretshare.math.combination;
    exports com.tiemens.secretshare.math.equation;
    exports com.tiemens.secretshare.math.matrix;
    exports com.tiemens.secretshare.math.type;
    exports com.tiemens.secretshare.md5sum;

}
