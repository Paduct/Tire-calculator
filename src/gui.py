# coding: utf-8
# Copyright 2017

"""Implementing the application."""

from glob import glob
from os import path
from sys import stderr
from typing import Dict, List

from kivy.app import App
from kivy.core.window import Window
from kivy.factory import Factory
from kivy.lang import Builder
from kivy.uix.label import Label
from widgetskv import WKV_ALL_FILES

from .calculator import Calculator


class Gui(App):

    """Class of the main window and runing of the application."""

    calculator: Calculator = Calculator()
    title: str = "Tire calculator"
    version: str = "0.1.0"
    create_year: int = 2017
    project_link: str = "https://github.com/Paduct/tire_calculator"
    license_link: str = "https://www.gnu.org/licenses/gpl-3.0"
    description: str = calculator.DESCRIPTION

    FRS: str = "{0:.2f}"
    SAVED: str = "Saved!"
    FORMAT_TEMPLATE: str = "{0}/{1} R{2} {3}{4}"
    LOAD_INDEX_STR: str = "Load index"
    SPEED_INDEX_STR: str = "Speed index"
    TIRE_WIDTH_STR: str = "Tire width (mm)"
    ASPECT_RATION_STR: str = "Aspect ration (%)"
    RIM_DIAMETER_STR: str = "Rim diameter (inch)"

    def build(self):
        """Accumulate of resources and the start of the main window."""
        project_path: str = path.split(path.dirname(__file__))[0]
        kv_files_path: str = path.join(project_path, "uix", "*.kv")
        kv_file_names: List[str] = glob(kv_files_path)
        kv_file_names.extend(WKV_ALL_FILES)

        for file_name in kv_file_names:
            Builder.load_file(file_name)

        Window.clearcolor = (0.2, 0.2, 0.2, 1)
        self.icon = path.join(project_path, "data", "car.png")
        self.root = Factory.RootWindow()
        self.calculate_parameters()

    def calculate_parameters(self):
        """Calculate and display of parameters."""
        self.root.ids.save_status.text = ''

        self.calculator.tire_width = int(self.root.ids.tire_width_old.text)
        self.calculator.aspect_ration = \
            int(self.root.ids.aspect_ration_old.text)
        self.calculator.rim_diameter = int(self.root.ids.diameter_rim_old.text)
        self.calculator.load_index = int(self.root.ids.load_index_old.text)
        self.calculator.speed_index = self.root.ids.speed_index_old.text
        old_tire: Dict[str, float] = self.calculator.tire_characteristics()

        self.calculator.tire_width = int(self.root.ids.tire_width_new.text)
        self.calculator.aspect_ration = \
            int(self.root.ids.aspect_ration_new.text)
        self.calculator.rim_diameter = int(self.root.ids.diameter_rim_new.text)
        self.calculator.load_index = int(self.root.ids.load_index_new.text)
        self.calculator.speed_index = self.root.ids.speed_index_new.text
        new_tire: Dict[str, float] = self.calculator.tire_characteristics()

        for key in old_tire:
            self.root.ids[f"{key}_old"].text = self.FRS.format(old_tire[key])
            self.root.ids[f"{key}_new"].text = self.FRS.format(new_tire[key])
            self.color_definition(self.root.ids[f"{key}_dif"],
                                  new_tire[key] - old_tire[key])

        self.color_definition(
            self.root.ids.clearance_dif,
            (new_tire["overall_diameter"] - old_tire["overall_diameter"]) / 2
        )

    def color_definition(self, dif_key: Label, dif_value: float):
        """Assign color."""
        dif_key.text = self.FRS.format(dif_value)
        if dif_value > 0.0:
            dif_key.color = (0, 1, 0, 1)
        elif dif_value < 0.0:
            dif_key.color = (1, 0, 0, 1)
        else:
            dif_key.color = (1, 1, 1, 1)

    def impl_path_chooser(self, path_file: str):
        """Implement menu - save the current result to a file."""
        try:
            with open(path_file, 'a') as files:
                files.write(self.calculator.tire_characteristics_form(
                    self.FORMAT_TEMPLATE.format(
                        self.root.ids.tire_width_old.text,
                        self.root.ids.aspect_ration_old.text,
                        self.root.ids.diameter_rim_old.text,
                        self.root.ids.load_index_old.text,
                        self.root.ids.speed_index_old.text
                    ),
                    self.FORMAT_TEMPLATE.format(
                        self.root.ids.tire_width_new.text,
                        self.root.ids.aspect_ration_new.text,
                        self.root.ids.diameter_rim_new.text,
                        self.root.ids.load_index_new.text,
                        self.root.ids.speed_index_new.text
                    )
                ))
                files.flush()
                self.root.ids.save_status.text = self.SAVED
        except OSError as error:
            stderr.write(f"{error}\n")

    def on_pause(self) -> bool:
        """Return the sign of switching to pause mode."""
        return True
