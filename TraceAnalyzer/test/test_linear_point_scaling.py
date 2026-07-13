import src.conversions.trace_conversions as tc
import pytest

def testInversePropOOBInitMinOOB():
    with pytest.raises(AssertionError):
        tc.PointsLinearIdeal(-1, 0)

def testPointsStepsInitMaxOOB():
    with pytest.raises(AssertionError):
        tc.PointsLinearIdeal(1, 0)

def testPointsStepsInitIdentical():
    with pytest.raises(AssertionError):
        tc.PointsLinearIdeal(0, 0)

def testPointsStepsInitValid():
    tc.PointsLinearIdeal(0, 1)
    tc.PointsLinearIdeal(1, 2)

def testPointsStepsScaleZeroDiffering():
    fn = tc.PointsLinearIdeal(0, 1)
    try:
        p = fn.get_points(0)
        assert(p == 1)
        p = fn.get_points(1)
        assert(p == 0)
        p = fn.get_points(2)
        assert(p == 0)
    except Exception:
        assert False

def testPointsStepsScaleNonZeroDiffering():
    fn = tc.PointsLinearIdeal(0, 10)
    try:
        p = fn.get_points(0)
        assert(p == 1)
        p = fn.get_points(5)
        assert(p == 0.5)
        p = fn.get_points(10)
        assert(p == 0)
        p = fn.get_points(12)
        assert(p == 0)
    except Exception:
        assert False