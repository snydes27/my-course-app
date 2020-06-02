package database;

public enum RecyclerContext
{
    MAIN {
        @Override
        public String toString() {
            return "Parent";
        }
    },

    CHILD {
        @Override
        public String toString() {
            return "Child";
        }
    },

    ADD {
        @Override
        public String toString() {
            return "Add";
        }
    }
}
