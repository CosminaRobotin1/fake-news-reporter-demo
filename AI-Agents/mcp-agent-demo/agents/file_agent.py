from pathlib import Path


def write_summary(content: str):
    output_file = Path("demo_data/summary.txt")

    with open(output_file, "w", encoding="utf-8") as f:
        f.write(content)

    return str(output_file)

#primește rezumatul și îl salvează în demo_data/summary.txt