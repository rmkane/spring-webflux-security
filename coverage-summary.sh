#!/bin/bash

# Script to display aggregated coverage summary in console

echo ""
echo "========================================================================"
echo "JaCoCo Coverage Summary"
echo "========================================================================"
echo ""

# Run tests first, then generate aggregated report
echo "Running tests and generating coverage reports..."
mvn clean test -rf app-parent > /dev/null 2>&1
mvn jacoco:report-aggregate > /dev/null 2>&1

echo ""
echo "Coverage by Module:"
echo "-------------------"

# Parse CSV files from each module
for module in app-api app-persistence app-security; do
    csv_file="${module}/target/site/jacoco/jacoco.csv"
    if [ -f "$csv_file" ]; then
        # Extract coverage data from CSV (skip header)
        coverage=$(tail -n +2 "$csv_file" | awk -F',' '{
            total_instructions = $4 + $5
            if (total_instructions > 0) {
                coverage_pct = ($5 / total_instructions) * 100
                printf "  %-20s: %.1f%% (%d/%d instructions)\n", $3, coverage_pct, $5, total_instructions
            }
        }')
        if [ -n "$coverage" ]; then
            echo "$module:"
            echo "$coverage"
        fi
    fi
done

echo ""
echo "Aggregated Summary:"
echo "--------------------"
# Calculate totals from individual module CSVs
totals=$(for module in app-api app-persistence app-security; do
    csv_file="${module}/target/site/jacoco/jacoco.csv"
    if [ -f "$csv_file" ]; then
        tail -n +2 "$csv_file"
    fi
done | awk -F',' 'BEGIN {
    inst_missed = 0
    inst_covered = 0
    line_missed = 0
    line_covered = 0
    branch_missed = 0
    branch_covered = 0
} {
    inst_missed += $4
    inst_covered += $5
    branch_missed += $6
    branch_covered += $7
    line_missed += $8
    line_covered += $9
} END {
    inst_total = inst_missed + inst_covered
    line_total = line_missed + line_covered
    branch_total = branch_missed + branch_covered
    if (inst_total > 0) {
        printf "%.1f %.0f %.0f %.1f %.0f %.0f %.1f %.0f %.0f", 
            (inst_covered / inst_total) * 100, inst_covered, inst_total,
            (line_covered / line_total) * 100, line_covered, line_total,
            (branch_covered / branch_total) * 100, branch_covered, branch_total
    }
}')

if [ -n "$totals" ]; then
    inst_pct=$(echo "$totals" | cut -d' ' -f1)
    inst_covered=$(echo "$totals" | cut -d' ' -f2)
    inst_total=$(echo "$totals" | cut -d' ' -f3)
    line_pct=$(echo "$totals" | cut -d' ' -f4)
    line_covered=$(echo "$totals" | cut -d' ' -f5)
    line_total=$(echo "$totals" | cut -d' ' -f6)
    branch_pct=$(echo "$totals" | cut -d' ' -f7)
    branch_covered=$(echo "$totals" | cut -d' ' -f8)
    branch_total=$(echo "$totals" | cut -d' ' -f9)
    
    echo "  Instructions: ${inst_pct}% (${inst_covered}/${inst_total})"
    echo "  Lines:        ${line_pct}% (${line_covered}/${line_total})"
    echo "  Branches:     ${branch_pct}% (${branch_covered}/${branch_total})"
else
    echo "  No coverage data found"
fi

if [ -f "target/site/jacoco-aggregate/index.html" ]; then
    echo ""
    echo "Full aggregated report: target/site/jacoco-aggregate/index.html"
fi

echo "========================================================================"

