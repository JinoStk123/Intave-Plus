package de.jpx3.intave.share;

public enum AxisRotation {
  NONE {
    public int cycle(int x, int y, int z, Direction.Axis axis) {
      return axis.select(x, y, z);
    }

    public Direction.Axis cycle(Direction.Axis axisIn) {
      return axisIn;
    }

    public AxisRotation inverse() {
      return this;
    }
  },
  FORWARD {
    public int cycle(int x, int y, int z, Direction.Axis axis) {
      return axis.select(z, x, y);
    }

    public Direction.Axis cycle(Direction.Axis axisIn) {
      return AXES[Math.floorMod(axisIn.ordinal() + 1, 3)];
    }

    public AxisRotation inverse() {
      return BACKWARD;
    }
  },
  BACKWARD {
    public int cycle(int x, int y, int z, Direction.Axis axis) {
      return axis.select(y, z, x);
    }

    public Direction.Axis cycle(Direction.Axis axisIn) {
      return AXES[Math.floorMod(axisIn.ordinal() - 1, 3)];
    }

    public AxisRotation inverse() {
      return FORWARD;
    }
  };

  public static final Direction.Axis[] AXES = Direction.Axis.values();
  public static final AxisRotation[] AXIS_ROTATIONS = values();

  AxisRotation() {
  }

  public abstract int cycle(int x, int y, int z, Direction.Axis axis);

  public abstract Direction.Axis cycle(Direction.Axis axisIn);

  public abstract AxisRotation inverse();

  public static AxisRotation differential(Direction.Axis a, Direction.Axis b) {
    return AXIS_ROTATIONS[Math.floorMod(b.ordinal() - a.ordinal(), 3)];
  }
}

