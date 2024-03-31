package carp.covanalyser.core.domain

interface ICoverage {
    var expectation: IExpectation
    var timeFrameSeconds: Int


    public abstract fun calculateCoverage(): Double


}