import src.conversions.trace_conversions as tc
import pytest

def testPointsStepsInitSingleZero():
    steps = [
        tc.PointThreshold(0, 1)
    ]
    tc.PointsSteps(steps)
    assert True

def testPointsStepsInitInvalidValuationUpper():
    steps = [
        tc.PointThreshold(0, 2)
    ]
    with pytest.raises(AssertionError):
        tc.PointsSteps(steps)


def testPointsStepsInitInvalidValuationLower():
    steps = [
        tc.PointThreshold(0, -1)
    ]
    with pytest.raises(AssertionError):
        tc.PointsSteps(steps)


def testPointsStepsInitInvalidThreshold():
    steps = [
        tc.PointThreshold(-1, 0)
    ]
    with pytest.raises(AssertionError):
        tc.PointsSteps(steps)

def testPointsStepsInitMultOrdered():
    steps = [
        tc.PointThreshold(0, 0),
        tc.PointThreshold(1, 0.5),
        tc.PointThreshold(2, 1)
    ]
    tc.PointsSteps(steps)
    assert True

def testPointsStepsInitMultUnorderedThreshold():
    steps = [
        tc.PointThreshold(0, 0),
        tc.PointThreshold(2, 0.5),
        tc.PointThreshold(1, 1),
    ]
    with pytest.raises(AssertionError):
        tc.PointsSteps(steps)

def testPointsStepsScaleMinRange():
    steps = [
        tc.PointThreshold(0, 0),
        tc.PointThreshold(1, 0.5),
        tc.PointThreshold(2, 1),
    ]
    fn = tc.PointsSteps(steps)
    assert fn.get_points(0) == 0


def testPointsStepsScaleMaxRange():
    steps = [
        tc.PointThreshold(0, 0),
        tc.PointThreshold(1, 0.5),
        tc.PointThreshold(2, 1),
    ]
    fn = tc.PointsSteps(steps)
    assert fn.get_points(2) == 1

def testPointsStepsScaleInRange():
    steps = [
        tc.PointThreshold(0, 0),
        tc.PointThreshold(1, 0.5),
        tc.PointThreshold(2, 1),
    ]
    fn = tc.PointsSteps(steps)
    assert fn.get_points(1) == 0.5

def testPointsStepsScaleOOBLower():
    steps = [
        tc.PointThreshold(0, 0),
        tc.PointThreshold(1, 0.5),
        tc.PointThreshold(2, 1),
    ]
    fn = tc.PointsSteps(steps)
    with pytest.raises(AssertionError):
        fn.get_points(-1)
