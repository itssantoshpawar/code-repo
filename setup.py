"""
Setup script for OSB Pipeline Framework
"""

from setuptools import setup, find_packages
from pathlib import Path

# Read the README file
readme_file = Path(__file__).parent / "osb_pipeline_framework" / "README.md"
long_description = ""
if readme_file.exists():
    long_description = readme_file.read_text(encoding="utf-8")

setup(
    name="osb-pipeline-framework",
    version="1.0.0",
    author="OSB Pipeline Framework Team",
    description="A flexible, configuration-driven framework for processing OSB pipelines",
    long_description=long_description,
    long_description_content_type="text/markdown",
    packages=find_packages(include=["osb_pipeline_framework", "osb_pipeline_framework.*"]),
    python_requires=">=3.7",
    install_requires=[
        # No required dependencies - framework uses only Python standard library
    ],
    extras_require={
        "yaml": ["PyYAML>=6.0"],  # Optional YAML support
        "dev": [
            "pytest>=7.0.0",
            "black>=22.0.0",
            "flake8>=4.0.0",
        ],
    },
    classifiers=[
        "Development Status :: 5 - Production/Stable",
        "Intended Audience :: Developers",
        "Programming Language :: Python :: 3",
        "Programming Language :: Python :: 3.7",
        "Programming Language :: Python :: 3.8",
        "Programming Language :: Python :: 3.9",
        "Programming Language :: Python :: 3.10",
        "Programming Language :: Python :: 3.11",
        "Programming Language :: Python :: 3.12",
        "Topic :: Software Development :: Libraries :: Python Modules",
    ],
    keywords="osb pipeline framework etl data-processing configuration-driven",
    package_data={
        "osb_pipeline_framework": [
            "examples/*.json",
            "schemas/*.json",
            "*.md",
        ],
    },
    include_package_data=True,
)
