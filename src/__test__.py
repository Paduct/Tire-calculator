# coding: utf-8
# Copyright 2017

"""Module for testing calculations."""

from unittest import TestCase, TestLoader, TestSuite

from calculator import Calculator


class TestCalculator(TestCase):

    """Parameter calculation testing."""

    def test_tire_characteristics_form(self):
        """Form filling and printing testing."""
        self.assertEqual(
            Calculator().tire_characteristics_form("205/55 R16 90S",
                                                   "225/45 R17 91V"),
            ("* - average value     205/55 R16 90S  225/45 R17 91V  Differ\n"
             "Sidewall height (mm)  112.75          101.25          -11.5\n"
             "*Rim width (inch)     7.07            7.86            0.79\n"
             "Overall diameter (mm) 631.9           634.3           2.4\n"
             "Rim diameter (mm)     406.4           431.8           25.4\n"
             "Circumference (mm)    1984.17         1991.7          7.54\n"
             "Revs per km           503.99          502.08          -1.91\n"
             "RPM at 100 km/h       839.98          836.81          -3.18\n"
             "*Contact area (cm²)   122.03          134.44          12.41\n"
             "Max load (kg)         600             615             15\n"
             "Max speed (km/h)      180             240             60\n"
             "Clearance (mm)        n/a             n/a             1.2\n")
        )


def suite() -> TestSuite:
    """Return a test suite for execution."""
    tests: TestSuite = TestSuite()
    loader: TestLoader = TestLoader()
    tests.addTest(loader.loadTestsFromTestCase(TestCalculator))
    return tests
