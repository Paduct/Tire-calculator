# coding: utf-8
# Copyright 2017

"""Module for calculating parameters."""

from argparse import ArgumentParser, Namespace
from re import match
from sys import stdout
from typing import Dict, List, Tuple


class Calculator():

    """Tire characterization."""

    red: str = '{0}'
    blue: str = '{0}'
    bold: str = '{0}'
    green: str = '{0}'
    yellow: str = '{0}'
    tire_width: int
    load_index: int
    speed_index: str
    rim_diameter: int
    aspect_ration: int

    OLD_HINT_TEXT: str = "Old size"
    NEW_HINT_TEXT: str = "New size"
    OLD_VALUE_DEFAULT: str = "205/55 R16 90S"
    NEW_VALUE_DEFAULT: str = "225/45 R17 91V"
    DESCRIPTION: str = "Application for calculating\nparameters of car tires."
    EPILOG: str = "Tire size input template (without quotes): '255/40 R17 94Y'"
    DIFFER_STR: str = "Differ"
    NOT_APPLICABLE_STR: str = "n/a"
    MAX_LOAD_STR: str = "Max load (kg)"
    REVS_PER_KM_STR: str = "Revs per km"
    CLEARANCE_STR: str = "Clearance (mm)"
    MAX_SPEED_STR: str = "Max speed (km/h)"
    RPM_AT_100_STR: str = "RPM at 100 km/h"
    RIM_WIDTH_STR: str = "*Rim width (inch)"
    RIM_DIAMETER_STR: str = "Rim diameter (mm)"
    AVERAGE_VALUE_STR: str = "* - average value"
    CONTACT_AREA_STR: str = "*Contact area (cm²)"
    CIRCUMFERENCE_STR: str = "Circumference (mm)"
    SIDEWALL_HEIGHT_STR: str = "Sidewall height (mm)"
    OVERALL_DIAMETER_STR: str = "Overall diameter (mm)"
    RESULT_TEMPLATE: str = ("{00} {01} {02} {03}\n"
                            "{04} {05} {06} {07}\n"
                            "{08} {09} {10} {11}\n"
                            "{12} {13} {14} {15}\n"
                            "{16} {17} {18} {19}\n"
                            "{20} {21} {22} {23}\n"
                            "{24} {25} {26} {27}\n"
                            "{28} {29} {30} {31}\n"
                            "{32} {33} {34} {35}\n"
                            "{36} {37} {38} {39}\n"
                            "{40} {41} {42} {43}\n"
                            "{44} {45} {46} {47}\n")
    PI: float = 3.14
    FIELD_WIDTH: int = 15
    FIRST_WIDTH: int = FIELD_WIDTH + 6
    INCH_MILLIMETERS: float = 25.40
    SQUARE_COEFFICIENT: float = 0.03
    ROTATION_COEFFICIENT: float = 1666666.6666667
    LOAD_INDEX_VALUE: Dict[int, int] = {
        40: 140, 41: 145, 42: 150, 43: 155, 44: 160, 45: 165, 46: 170, 47: 175,
        48: 180, 49: 185, 50: 190, 51: 195, 52: 200, 53: 206, 54: 212, 55: 218,
        56: 224, 57: 230, 58: 236, 59: 243, 60: 250, 61: 257, 62: 265, 63: 272,
        64: 280, 65: 290, 66: 300, 67: 307, 68: 315, 69: 325, 70: 335, 71: 345,
        72: 355, 73: 365, 74: 375, 75: 387, 76: 400, 77: 412, 78: 425, 79: 437,
        80: 450, 81: 462, 82: 475, 83: 487, 84: 500, 85: 515, 86: 530, 87: 545,
        88: 560, 89: 580, 90: 600, 91: 615, 92: 630, 93: 650, 94: 670, 95: 690,
        96: 710, 97: 730, 98: 750, 99: 775, 100: 800, 101: 825, 102: 850,
        103: 875, 104: 900, 105: 925, 106: 950, 107: 975, 108: 1000, 109: 1030,
        110: 1060, 111: 1090, 112: 1120, 113: 1150, 114: 1180, 115: 1215,
        116: 1250, 117: 1285, 118: 1320, 119: 1360, 120: 1400, 121: 1450,
        122: 1500, 123: 1550, 124: 1600, 125: 1650, 126: 1700, 127: 1750,
        128: 1800, 129: 1850, 130: 1900
    }
    SPEED_INDEX_VALUE: Dict[str, int] = {
        'B': 50, 'C': 60, 'D': 65, 'E': 70, 'F': 80, 'G': 90, 'J': 100,
        'K': 110, 'L': 120, 'M': 130, 'N': 140, 'P': 150, 'Q': 160, 'R': 170,
        'S': 180, 'T': 190, 'U': 200, 'H': 210, 'V': 240, 'Z': 255, 'W': 270,
        'Y': 300
    }
    TIRE_WIDTHS: List[str] = [str(i) for i in range(125, 366, 10)]
    LOAD_INDEXES: List[str] = [str(i) for i in sorted(LOAD_INDEX_VALUE.keys())]
    RIM_DIAMETERS: List[str] = [str(i) for i in range(10, 26)]
    SPEED_INDEXES: List[str] = sorted(SPEED_INDEX_VALUE.keys())
    ASPECT_RATIOS: List[str] = [str(i) for i in range(20, 91, 5)]

    def tire_characteristics(self) -> Dict[str, float]:
        """Return the calculated tire characteristics."""
        tire_characteristics: Dict[str, float] = {}

        tire_characteristics["sidewall_height"] = \
            self.tire_width * self.aspect_ration / 100
        tire_characteristics["rim_width"] = \
            self.tire_width / self.INCH_MILLIMETERS - 1
        tire_characteristics["rim_diameter"] = \
            self.rim_diameter * self.INCH_MILLIMETERS
        tire_characteristics["overall_diameter"] = \
            tire_characteristics["sidewall_height"] * 2 \
            + tire_characteristics["rim_diameter"]
        tire_characteristics["circumference"] = \
            tire_characteristics["overall_diameter"] * self.PI
        tire_characteristics["revs_per_km"] = \
            1000 / (tire_characteristics["circumference"] / 1000)
        tire_characteristics["rpm_hundred"] = \
            self.ROTATION_COEFFICIENT / tire_characteristics["circumference"]
        tire_characteristics["contact_area"] = \
            tire_characteristics["circumference"] \
            * self.SQUARE_COEFFICIENT * self.tire_width / 100
        tire_characteristics["max_load"] = \
            self.LOAD_INDEX_VALUE[self.load_index]
        tire_characteristics["max_speed"] = \
            self.SPEED_INDEX_VALUE[self.speed_index]

        return tire_characteristics

    def tire_characteristics_form(self, old_size: str, new_size: str) -> str:
        """Return completed print form."""
        old_tire: Dict[str, float] = self.parse_input_tire_size(old_size)
        new_tire: Dict[str, float] = self.parse_input_tire_size(new_size)
        difference_overall_diameter: float = \
            new_tire["overall_diameter"] - old_tire["overall_diameter"]

        return self.RESULT_TEMPLATE.format(
            self.yellow.format(self.AVERAGE_VALUE_STR.ljust(self.FIRST_WIDTH)),
            self.blue.format(old_size.ljust(self.FIELD_WIDTH)),
            self.blue.format(new_size.ljust(self.FIELD_WIDTH)),
            self.bold.format(self.DIFFER_STR),
            self.SIDEWALL_HEIGHT_STR.ljust(self.FIRST_WIDTH),
            *self.color_differ(old_tire["sidewall_height"],
                               new_tire["sidewall_height"]),
            self.RIM_WIDTH_STR.ljust(self.FIRST_WIDTH),
            *self.color_differ(old_tire["rim_width"],
                               new_tire["rim_width"]),
            self.OVERALL_DIAMETER_STR.ljust(self.FIRST_WIDTH),
            *self.color_differ(old_tire["overall_diameter"],
                               new_tire["overall_diameter"]),
            self.RIM_DIAMETER_STR.ljust(self.FIRST_WIDTH),
            *self.color_differ(old_tire["rim_diameter"],
                               new_tire["rim_diameter"]),
            self.CIRCUMFERENCE_STR.ljust(self.FIRST_WIDTH),
            *self.color_differ(old_tire["circumference"],
                               new_tire["circumference"]),
            self.REVS_PER_KM_STR.ljust(self.FIRST_WIDTH),
            *self.color_differ(old_tire["revs_per_km"],
                               new_tire["revs_per_km"]),
            self.RPM_AT_100_STR.ljust(self.FIRST_WIDTH),
            *self.color_differ(old_tire["rpm_hundred"],
                               new_tire["rpm_hundred"]),
            self.CONTACT_AREA_STR.ljust(self.FIRST_WIDTH),
            *self.color_differ(old_tire["contact_area"],
                               new_tire["contact_area"]),
            self.MAX_LOAD_STR.ljust(self.FIRST_WIDTH),
            *self.color_differ(old_tire["max_load"],
                               new_tire["max_load"]),
            self.MAX_SPEED_STR.ljust(self.FIRST_WIDTH),
            *self.color_differ(old_tire["max_speed"],
                               new_tire["max_speed"]),
            self.CLEARANCE_STR.ljust(self.FIRST_WIDTH),
            self.blue.format(self.NOT_APPLICABLE_STR.ljust(self.FIELD_WIDTH)),
            self.blue.format(self.NOT_APPLICABLE_STR.ljust(self.FIELD_WIDTH)),
            self.color(round(difference_overall_diameter / 2, 2))
        )

    def parse_input_tire_size(self, tire_size: str) -> Dict[str, float]:
        """Return the characteristics of the input tire size."""
        assert match(r"^({0})/({1}) R({2}) ({3})({4})$".format(
            '|'.join(self.TIRE_WIDTHS), '|'.join(self.ASPECT_RATIOS),
            '|'.join(self.RIM_DIAMETERS), '|'.join(self.LOAD_INDEXES),
            '|'.join(self.SPEED_INDEXES)
        ), tire_size), tire_size

        self.tire_width = int(tire_size[0:3])
        self.aspect_ration = int(tire_size[4:6])
        self.rim_diameter = int(tire_size[8:10])
        self.load_index = int(tire_size[11:-1])
        self.speed_index = tire_size[-1]

        return self.tire_characteristics()

    def color_differ(self, old_value: float,
                     new_value: float) -> Tuple[str, str, str]:
        """Return colored and aligned strings of values."""
        return (
            self.bold.format(str(round(old_value, 2)).ljust(self.FIELD_WIDTH)),
            self.bold.format(str(round(new_value, 2)).ljust(self.FIELD_WIDTH)),
            self.color(round(new_value - old_value, 2))
        )

    def color(self, dif_value: float) -> str:
        """Return string with assigned color."""
        differ: str

        if dif_value > 0.0:
            differ = self.green.format(dif_value)
        elif dif_value < 0.0:
            differ = self.red.format(dif_value)
        else:
            differ = self.bold.format(dif_value)

        return differ


if __name__ == "__main__":
    parser: ArgumentParser = ArgumentParser(description=Calculator.DESCRIPTION,
                                            epilog=Calculator.EPILOG)
    parser.add_argument("-c", dest="colors", help="turn color design",
                        action="store_true")
    args: Namespace = parser.parse_args()

    if args.colors:
        Calculator.red = "\x1b[31;1m{0}\x1b[0m"
        Calculator.blue = "\x1b[34;1m{0}\x1b[0m"
        Calculator.green = "\x1b[32;1m{0}\x1b[0m"
        Calculator.yellow = "\x1b[33;1m{0}\x1b[0m"
        Calculator.bold = "\x1b[0;1m{0}\x1b[0m"

    stdout.write(Calculator().tire_characteristics_form(
        input(f"{Calculator.OLD_HINT_TEXT}: ") or Calculator.OLD_VALUE_DEFAULT,
        input(f"{Calculator.NEW_HINT_TEXT}: ") or Calculator.NEW_VALUE_DEFAULT
    ))
