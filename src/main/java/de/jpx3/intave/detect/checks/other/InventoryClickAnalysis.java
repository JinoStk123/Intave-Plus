package de.jpx3.intave.detect.checks.other;

import de.jpx3.intave.IntavePlugin;
import de.jpx3.intave.detect.Check;
import de.jpx3.intave.detect.CheckViolationLevelDecrementer;
import de.jpx3.intave.detect.checks.other.inventoryclickanalysis.InventoryClickDelayAnalyzer;
import de.jpx3.intave.detect.checks.other.inventoryclickanalysis.InventoryClickNotOpenCheck;
import de.jpx3.intave.detect.checks.other.inventoryclickanalysis.InventoryClickOnMoveCheck;
import de.jpx3.intave.detect.checks.other.inventoryclickanalysis.InventoryClickPacketDelayAnalyzer;

public final class InventoryClickAnalysis extends Check {
  public final static double MAX_VL_DECREMENT_PER_SECOND = 1;
  private final CheckViolationLevelDecrementer decrementer;
  private final boolean highToleranceMode;

  public InventoryClickAnalysis(IntavePlugin plugin) {
    super("InventoryClickAnalysis", "inventoryclickanalysis");
    decrementer = new CheckViolationLevelDecrementer(this, MAX_VL_DECREMENT_PER_SECOND);
    this.highToleranceMode = configuration().settings().boolBy("high-tolerance", true);
    this.setupCheckParts();
  }

  private void setupCheckParts() {
    appendCheckPart(new InventoryClickOnMoveCheck(this));
    appendCheckPart(new InventoryClickNotOpenCheck(this));
    appendCheckPart(new InventoryClickDelayAnalyzer(this, highToleranceMode));
    appendCheckPart(new InventoryClickPacketDelayAnalyzer(this));
  }
}