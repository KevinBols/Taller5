/**
 *
 */
package net.sourceforge.ganttproject.gui;

import java.util.List;


public interface TableHeaderUIFacade {
    int getSize();
    Column getField(int index);
    void clear();
    void add(String name, int order, int width);
    void importData(TableHeaderUIFacade source);

    public interface Column {
        String getID();
        String getName();
        int getOrder();
        int getWidth();
        boolean isVisible();
        void setVisible(boolean visible);
        void setOrder(int order);
    }

    class ColumnStub implements TableHeaderUIFacade.Column {
        private final String myID;
        private int myOrder;
        private final int myWidth;
        private final String myName;
        private final boolean isVisible;

        public ColumnStub(String id, String name, boolean visible, int order, int width) {
            myName = name;
            myID = id;
            myOrder = order;
            myWidth = width;
            isVisible = visible;
        }
        public String getID() {
            return myID;
        }
        public int getOrder() {
            return myOrder;
        }
        public int getWidth() {
            return myWidth;
        }
        public boolean isVisible() {
            return isVisible;
        }
        public String getName() {
            return myName;
        }
        @Override
        public void setVisible(boolean visible) {
            throw new UnsupportedOperationException();
        }
        public void setOrder(int order) {
            myOrder = order;
        }
        public String toString() {
            return myID;
        }


    }

    class Immutable {
        public static TableHeaderUIFacade fromList(final List<Column> columns) {
            return new TableHeaderUIFacade() {
                public int getSize() {
                    return columns.size();
                }

                public Column getField(int index) {
                    return columns.get(index);
                }

                public void clear() {
                    throw new UnsupportedOperationException();
                }

                public void add(String name, int order, int width) {
                    throw new UnsupportedOperationException();
                }

                public void importData(TableHeaderUIFacade source) {
                    throw new UnsupportedOperationException();
                }
            };
        }
    }
}