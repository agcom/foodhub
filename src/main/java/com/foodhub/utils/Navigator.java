package com.foodhub.utils;

import java.util.LinkedList;

public class Navigator {

    private LinkedList<Navi> navis = new LinkedList<>();
    private int currentNaviIndex = -1;

    public Navigator() {

    }

    public void addNavi(Navigable navigable, Navigator child) {

        addNavi(new Navi(navigable, child));

    }

    public void addNavi(Navi navi) {

        for (int i = currentNaviIndex + 1; i < navis.size(); i++) {

            navis.remove(i);

        }

        navis.add(navi);
        currentNaviIndex++;

    }

    public void addFutureNavi(Navi navi) {

        navis.add(navi);

    }

    public boolean back() {

        if(currentNaviIndex < 0) return false;

        //check child call
        boolean childCall = navis.get(currentNaviIndex).getChild() != null && navis.get(currentNaviIndex).getChild().back();

        if(childCall) return true;
        else {//this call

            if(currentNaviIndex > 0) {

                return navis.get(--currentNaviIndex).getNavigable().constructThis();

            } else return false;

        }

    }

    public boolean forward() {

        if(currentNaviIndex < 0) return false;

        //check child call
        boolean childCall = navis.get(currentNaviIndex).getChild() != null && navis.get(currentNaviIndex).getChild().forward();

        if(childCall) return true;
        else {//this call

            if(navis.size() - 1 > currentNaviIndex) {

                return navis.get(++currentNaviIndex).getNavigable().constructThis();

            } else return false;

        }

    }

    public static class Navi {

        private Navigable navigable;
        private Navigator child;

        public Navi(Navigable navigable, Navigator child) {
            this.navigable = navigable;
            this.child = child;
        }

        public Navigable getNavigable() {
            return navigable;
        }

        public void setNavigable(Navigable navigable) {
            this.navigable = navigable;
        }

        public Navigator getChild() {
            return child;
        }

        public void setChild(Navigator child) {
            this.child = child;
        }
    }

}
